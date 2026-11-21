package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import org.spongepowered.asm.mixin.*;

@Mixin(EndermanRenderState.class)
abstract class RenderStateMixin implements JawState {
    @Unique private boolean undertow$gent;
    public boolean undertow$gentleman() { return undertow$gent; }
    public void undertow$gentleman(boolean value) { undertow$gent = value; }
    @Unique private float undertow$open;
    @Unique private float undertow$reach;
    public float undertow$grip() { return undertow$reach; }
    public void undertow$grip(float grip) { undertow$reach = grip; }
    public float undertow$jaw() { return undertow$open; }
    public void undertow$jaw(float open) { undertow$open=open; }
}
