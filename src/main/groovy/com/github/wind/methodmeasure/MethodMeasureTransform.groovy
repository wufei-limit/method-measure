package com.github.wind.methodmeasure

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.android.utils.FileUtils
import com.github.wind.methodmeasure.utils.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.concurrent.Callable

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

class MethodMeasureTransform extends Transform {

    private WaitableExecutor waitableExecutor
    private static String LOG_NAME = ""
    MethodMeasureTransform() {
        super()
        waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool()
    }

    def static setLogName(String name){
        LOG_NAME = name
    }

    @Override
    String getName() {
        return getClass().getName()
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        //只处理类文件
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        //不关心第三方库
        return TransformManager.PROJECT_ONLY
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        transformClass(transformInvocation.getContext(), transformInvocation.getInputs(), transformInvocation.getOutputProvider(), transformInvocation.isIncremental())
    }

    private void transformClass(Context context, Collection<TransformInput> inputs,
                                TransformOutputProvider outputProvider, boolean incremental)
            throws TransformException, InterruptedException, IOException {
        long startTime = System.currentTimeMillis()
        //删除之前的输出
        if (outputProvider != null)
            outputProvider.deleteAll()
        //遍历inputs
        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                waitableExecutor.execute(new Callable<Object>() {
                    @Override
                    Object call() throws Exception {
                        transformDics(directoryInput, outputProvider)
                        return null
                    }
                })
            }

            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                waitableExecutor.execute(new Callable<Object>() {
                    @Override
                    Object call() throws Exception {
                        transformJars(jarInput, outputProvider)
                        return null
                    }
                })
            }
        }
        waitableExecutor.waitForTasksWithQuickFail(true)
        long dur = System.currentTimeMillis() - startTime
        Logger.warn("method measure process cast " + dur + "ms")
    }

    private static void transformDics(DirectoryInput directoryInput,
                                      TransformOutputProvider outputProvider)
            throws TransformException, InterruptedException, IOException {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                if (needProcessFile(name)) {
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new MethodMeasureClassVisitor(classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    String lastName = file.parentFile.absolutePath + File.separator + name
                    FileOutputStream fos = new FileOutputStream(lastName)
                    fos.write(code)
                    fos.close()
                }
            }
        }
        //处理完输入文件之后，要把输出给下一个任务
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    private static boolean needProcessFile(String name) {
        //只处理需要的class文件
        return (name.endsWith(".class") && !name.contains("R.class") && !name.contains("R\$")
                && "BuildConfig.class" != name
                && !name.contains("android/") && !name.contains("androidx/") && !name.contains(LOG_NAME))
    }

    private static void transformJars(JarInput jarInput, TransformOutputProvider outputProvider)
            throws IOException {
        // 第三方jar包，不做处理
        //处理完输入文件之后，要把输出给下一个任务
        File dest = outputProvider.getContentLocation(jarInput.getName(),
                jarInput.getContentTypes(), jarInput.getScopes(),
                Format.JAR)
        FileUtils.copyFile(jarInput.getFile(), dest)
    }
}