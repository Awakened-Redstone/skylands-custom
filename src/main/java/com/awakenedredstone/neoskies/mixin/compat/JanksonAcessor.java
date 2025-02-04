package com.awakenedredstone.neoskies.mixin.compat;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.Marshaller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Jankson.class, remap = false)
public interface JanksonAcessor {
    @Invoker("<init>") static Jankson createJankson(Jankson.Builder builder) {
        throw new IllegalStateException("Mixin failed to apply");
    }

    @Accessor void setMarshaller(Marshaller marshaller);
    @Accessor void setAllowBareRootObject(boolean allowBareRootObject);
}
