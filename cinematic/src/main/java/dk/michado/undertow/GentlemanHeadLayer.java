package dk.michado.undertow;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.resources.Identifier;

public final class GentlemanHeadLayer extends CustomHeadLayer<EndermanRenderState, EndermanModel<EndermanRenderState>> {
    public static final Identifier MODEL = Identifier.parse("ender_undertow:gentleman");

    public GentlemanHeadLayer(RenderLayerParent<EndermanRenderState, EndermanModel<EndermanRenderState>> parent,
                              EntityRendererProvider.Context context) {
        super(parent, context.getModelSet(), context.getPlayerSkinRenderCache());
    }

    @Override
    public void submit(PoseStack pose, SubmitNodeCollector collector, int light, EndermanRenderState state, float yaw, float pitch) {
        if (((JawState)state).undertow$gentleman()) super.submit(pose, collector, light, state, yaw, pitch);
    }
}
