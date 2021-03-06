package com.gb.apm.profiler.instrument.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.gb.apm.bootstrap.core.config.ProfilerConfig;
import com.gb.apm.profiler.instrument.ASMBytecodeDumpService;
import com.gb.apm.profiler.instrument.BytecodeDumpService;

/**
 * @author Woonduk Kang(emeroad)
 */
public class BytecodeDumpTransformer implements ClassFileTransformer {

    private final ClassFileTransformer delegate;

    private final BytecodeDumpService bytecodeDumpService;

    public static ClassFileTransformer wrap(ClassFileTransformer classFileTransformer, ProfilerConfig profilerConfig) {
        return new BytecodeDumpTransformer(classFileTransformer, profilerConfig);
    }

    private BytecodeDumpTransformer(ClassFileTransformer delegate, ProfilerConfig profilerConfig) {
        if (delegate == null) {
            throw new NullPointerException("delegate must not be null");
        }
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig must not be null");
        }

        this.delegate = delegate;
        this.bytecodeDumpService = new ASMBytecodeDumpService(profilerConfig);

    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        byte[] transformBytes = null;
        boolean success = false;
        try {
            transformBytes = delegate.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            success = true;
            return transformBytes;
        } finally {
            this.bytecodeDumpService.dumpBytecode("original bytecode dump", className, classfileBuffer, loader);

            final boolean bytecodeChanged = isChanged(classfileBuffer, transformBytes);
            if (success && bytecodeChanged) {
                this.bytecodeDumpService.dumpBytecode("transform bytecode dump", className, transformBytes, loader);
            }
        }
    }

    private boolean isChanged(byte[] classfileBuffer, byte[] transformBytes) {
        if (transformBytes == null) {
            return false;
        }
        if (classfileBuffer == transformBytes) {
            return false;
        }
        return true;
    }


}
