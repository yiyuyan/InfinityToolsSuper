package fox.spiteful.avaritia.compat.ticon;

import fox.spiteful.avaritia.Lumberjack;

public class TonkersClient {

	public static void dunkThosePaintbrushes() {
		Lumberjack.info("Registering TiCon renderer mappings");
		Tonkers.neutronium.setRenderInfo(0x303030);
		Tonkers.infinityMetal.setRenderInfo(0xFFFFFF);
		//TConstructClientRegistry.addMaterialRenderMapping(Tonkers.neutroniumId, "tinker", Tonkers.neutroniumName, true);
		//TConstructClientRegistry.addMaterialRenderMapping(Tonkers.infinityMetalId, "tinker", Tonkers.infinityMetalName, true);

		NeutroniumIcons iconN = new NeutroniumIcons();
		iconN.register();
		InfinityIcons iconI = new InfinityIcons();
		iconI.register();
	}
}
