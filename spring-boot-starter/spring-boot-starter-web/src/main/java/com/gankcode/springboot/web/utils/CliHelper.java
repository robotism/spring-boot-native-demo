package com.gankcode.springboot.web.utils;

import com.gankcode.springboot.utils.FileManager;
import com.gankcode.springboot.utils.PlatformUtils;
import com.gankcode.springboot.utils.Shell;
import com.gankcode.springboot.utilsdev.DevUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Configuration
public class CliHelper {

    private static final Map<String, File> CACHE = new HashMap<>();

    private static CliProperties cliProperties = new CliProperties();


    @Autowired
    public void init(CliProperties cliProperties) {
        CliHelper.cliProperties = cliProperties;
        preset();
    }

    private static void preset() {
        try {
            final Resource[] resources = FileManager.getResources(getPlatformToolPath() + "/*");
            for (Resource resource : resources) {
                find(Objects.requireNonNull(resource.getFilename()));
            }
        } catch (Exception e) {
            DevUtils.exit(e);
        }
    }

    public static String execute(String toolName, String... args) {
        final File command = find(toolName);
        if (command == null) {
            return "command not found :" + toolName;
        }
        final List<String> commands = new ArrayList<>();
        commands.add(command.getAbsolutePath());
        commands.addAll(Arrays.asList(args));
        return Shell.execute(commands.toArray(new String[0]));
    }

    public static String getCrossToolPath() {
        return "tools/cross";
    }

    public static String getPlatformToolPath() {
        final String path;
        if (PlatformUtils.IS_WINDOWS) {
            path = "tools/windows";
        } else if (PlatformUtils.IS_LINUX) {
            path = "tools/linux";
        } else if (PlatformUtils.IS_DARWIN) {
            path = "tools/mac";
        } else {
            DevUtils.exit("platform not supported : " + PlatformUtils.OS_NAME);
            return null;
        }
        return path;
    }

    public static File find(final String name) {
        final File cache = CACHE.get(name);
        if (cache != null && cache.exists()) {
            return cache;
        }
        try {
            final File command = findByName(name);
            if (command != null) {
                CACHE.put(name, command);
            }
            return command;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }


    private static File findByName(final String name) {
        final String ext = PlatformUtils.IS_WINDOWS ? ".exe" : "";
        final String cmd = name.endsWith(ext) ? name : name + ext;

        final int depth = cliProperties.getDepth() != null ? cliProperties.getDepth() : 3;
        final Set<String> path = cliProperties.getPath() != null ? cliProperties.getPath() : new HashSet<>();
        // 指定环境获取
        for (final String dir : path) {
            try {
                final File folder = new File(dir);
                final File file = findByFolder(folder, depth, name);
                log.info("cli find `{}` @path = {} | {}", name, folder, file != null && file.exists());
                if (file != null && file.exists()) {
                    return file;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        // 系统环境获取
        try {
            final File sys = findByEnv(name);
            if (sys != null && sys.exists()) {
                return sys;
            }
        } catch (Exception e) {
            log.error("", e);
        }

        // 内置资源获取
        final String[] resources = new String[]{
                getPlatformToolPath() + "/" + name,
                getPlatformToolPath() + "/" + cmd,
                getCrossToolPath() + "/" + name,
                getCrossToolPath() + "/" + cmd,
        };
        for (String resource : resources) {
            try {
                final File res = findByResource(resource);
                if (res.exists()) {
                    return res;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return null;
    }

    private static File findByFolder(final File folder, final int depth, final String name) {
        try {
            final File file = new File(folder, name);
            if (file.exists()) {
                return file;
            }
        } catch (Exception e) {
            log.error("", e);
        }
        if (depth == 0) {
            return null;
        }
        try {
            final File[] files = folder.listFiles(File::isDirectory);
            if (files == null) {
                return null;
            }
            for (File c : files) {
                final File file = findByFolder(c, depth - 1, name);
                if (file != null) {
                    return file;
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    private static File findByResource(final String path) throws Exception {
        final File target = new File(FileManager.DIR_RESOURCES, path);
        final File folder = target.getParentFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("create directory error: " + folder);
        }
        final Resource resource = FileManager.getResource(path);
        if (resource != null && resource.exists()) {
            if (target.exists() && target.length() != resource.contentLength()) {
                target.delete();
            }
            if (!target.exists() && !target.createNewFile()) {
                throw new IOException("create file error: " + target);
            }
            if (target.exists() && target.length() == 0) {
                FileCopyUtils.copy(resource.getInputStream(), Files.newOutputStream(target.toPath()));
            }
            if (target.exists() && !target.canExecute() && !target.setExecutable(true)) {
                throw new IOException("chmod file error: " + target);
            }
        }
        return target;
    }

    private static File findByEnv(String name) {

        final String path;
        final String result;
        if (!PlatformUtils.IS_WINDOWS) {
            result = Shell.execute("/usr/bin/which " + name);
        } else {
            result = Shell.execute("Get-Command " + name);
        }
        if (!StringUtils.hasText(result)) {
            path = null;
        } else {
            final String regex = "([A-Za-z]:\\\\.*\\.exe)";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                path = matcher.group(1);
            } else {
                path = result;
            }
        }

        final File file = path == null ? null : new File(path.trim());

        if (file != null && file.exists() && file.canExecute()) {
            return file;
        } else {
            return null;
        }
    }


    @Data
    @Configuration
    @ConfigurationProperties("cli")
    public static class CliProperties {
        private Integer depth = 3;
        private Set<String> path = new HashSet<>();
    }

}
