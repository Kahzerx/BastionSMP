package bastion.mixins;

import bastion.Bastion;
import bastion.discord.utils.DiscordListener;
import bastion.utils.FileManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class BastionServerRunMixin {
    @Shadow @Final protected LevelStorage.Session session;

    @Inject(method = "runServer", at = @At("HEAD"))
    public void run (CallbackInfo ci){
        try {
            FileManager.initializeYaml();  // Cargar la configuración del archivo .yaml
            if (Bastion.config.chatChannelId != 0 && !Bastion.config.discordToken.equals("")) {
                if (Bastion.config.isRunning) {  // Iniciar el bot de discord.
                    try {
                        DiscordListener.connect((MinecraftServer) (Object) this, Bastion.config.discordToken, String.valueOf(Bastion.config.chatChannelId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("config file not created");
            e.printStackTrace();
        }
    }

    @Inject(method = "runServer", at = @At("RETURN"))
    public void stop (CallbackInfo ci){
        if (DiscordListener.chatBridge) DiscordListener.stop();
    }
}
