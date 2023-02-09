package net.minecraftforge.gradle.common.runtime.tasks;

import net.minecraftforge.gradle.base.util.ZipBuildingFileTreeVisitor;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

@CacheableTask
public abstract class ClientExtraJar extends DefaultRuntime {

    public ClientExtraJar() {
        super();
        getOutputFileName().set("client-extra.jar");
    }

    @TaskAction
    public void doTask() throws Throwable {
        final File clientJar = getOriginalClientJar().get().getAsFile();
        final File outputJar = getOutput().getAsFile().get();

        outputJar.getParentFile().mkdirs();

        final FileTree inputTree = getProject().zipTree(clientJar);
        final FileTree filteredInput = inputTree.matching(filter -> {
            filter.exclude("**/*.class");
        });

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar))) {
            filteredInput.visit(new ZipBuildingFileTreeVisitor(zos));
        }
    }

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract RegularFileProperty getOriginalClientJar();
}
