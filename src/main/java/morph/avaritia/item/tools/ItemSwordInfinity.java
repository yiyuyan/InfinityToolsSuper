package morph.avaritia.item.tools;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.util.TransformUtils;
import com.google.common.base.Predicate;
import morph.avaritia.Avaritia;
import morph.avaritia.api.ICosmicRenderItem;
import morph.avaritia.api.registration.IModelRegister;
import morph.avaritia.client.render.item.CosmicItemRender;
import morph.avaritia.entity.EntityImmortalItem;
import morph.avaritia.handler.AvaritiaEventHandler;
import morph.avaritia.init.AvaritiaTextures;
import morph.avaritia.init.ModItems;
import morph.avaritia.util.DamageSourceInfinitySword;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemSwordInfinity extends ItemSword implements ICosmicRenderItem, IModelRegister {

    private static final ToolMaterial TOOL_MATERIAL = EnumHelper.addToolMaterial("INFINITY_SWORD", 32, -1, 9999F, -3.0F, 200);
    //private IIcon cosmicMask;
    //private IIcon pommel;

    public ItemSwordInfinity() {
        super(TOOL_MATERIAL);
        setUnlocalizedName("avaritia:infinity_sword");
        setRegistryName("infinity_sword");
        setCreativeTab(Avaritia.tab);
    }

    @Override
    public void onUpdate(ItemStack p_onUpdate_1_, World p_onUpdate_2_, Entity p_onUpdate_3_, int p_onUpdate_4_, boolean p_onUpdate_5_) {
        if(p_onUpdate_3_ instanceof EntityLivingBase){
            EntityLivingBase entityLivingBase = (EntityLivingBase)p_onUpdate_3_;
            entityLivingBase.hurtTime=0;
            entityLivingBase.deathTime=0;
            entityLivingBase.isDead = false;
            entityLivingBase.setFire(0);
            entityLivingBase.setHealth(entityLivingBase.getMaxHealth());
            p_onUpdate_1_.setItemDamage(0);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase player) {
        if (player.world.isRemote) {
            return true;
        }
        if (victim instanceof EntityPlayer) {
            EntityPlayer pvp = (EntityPlayer) victim;
            if (AvaritiaEventHandler.isInfinite(pvp)) {
                victim.attackEntityFrom(new DamageSourceInfinitySword(player).setDamageBypassesArmor(), 4.0F);
                return true;
            }
            if (pvp.getHeldItem(EnumHand.MAIN_HAND) != null && pvp.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.infinity_sword && pvp.isHandActive()) {
                return true;
            }
        }

        victim.recentlyHit = 60;
        victim.getCombatTracker().trackDamage(new DamageSourceInfinitySword(player), victim.getHealth(), victim.getHealth());
        victim.setHealth(-1);
        victim.onDeath(new EntityDamageSource("infinity", player));
        victim.arrowHitTimer = 0;
        victim.setAIMoveSpeed(0F);
        if(!victim.isDead){
            victim.setDead();
            if(!victim.isDead){
                victim.sendEndCombat();
                victim.addedToChunk = false;
                victim.getEntityWorld().removeEntity(victim);
                if(!victim.isDead){
                    victim.move(MoverType.PISTON,0,Double.NaN,0);
                    victim.move(MoverType.SELF,0,Double.NaN,0);
                    victim.move(MoverType.PLAYER,0,Double.NaN,0);
                    victim.isDead = true;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer victim = (EntityPlayer) entity;
            if (victim.capabilities.isCreativeMode && !victim.isDead && victim.getHealth() > 0 && !AvaritiaEventHandler.isInfinite(victim)) {
                victim.getCombatTracker().trackDamage(new DamageSourceInfinitySword(player), victim.getHealth(), victim.getHealth());
                victim.setHealth(-1);
                victim.onDeath(new EntityDamageSource("infinity", player));
                //TODO
                //player.addStat(Achievements.creative_kill, 1);

                //add
                victim.arrowHitTimer = 0;
                victim.setAIMoveSpeed(0F);
                if(!victim.isDead){
                    victim.setDead();
                    if(!victim.isDead){
                        victim.sendEndCombat();
                        victim.addedToChunk = false;
                        victim.getEntityWorld().removeEntity(victim);
                        if(!victim.isDead){
                            victim.move(MoverType.PISTON,0,Double.NaN,0);
                            victim.move(MoverType.SELF,0,Double.NaN,0);
                            victim.move(MoverType.PLAYER,0,Double.NaN,0);
                            victim.isDead = true;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer p_onItemUse_1_, World p_onItemUse_2_, BlockPos p_onItemUse_3_, EnumHand p_onItemUse_4_, EnumFacing p_onItemUse_5_, float p_onItemUse_6_, float p_onItemUse_7_, float p_onItemUse_8_) {
        for(Entity entity:p_onItemUse_2_.getEntities(Entity.class, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return true;
            }

            @Override
            public boolean equals(@Nullable Object object) {
                return object==this;
            }
        })){
            if(entity instanceof EntityLivingBase){
                EntityLivingBase victim = (EntityLivingBase)entity;
                if(!victim.getHeldItemMainhand().getItem().equals(this) && !victim.getHeldItemOffhand().getItem().equals(this)){
                    victim.setHealth(-1);
                    victim.onDeath(new EntityDamageSource("infinity", entity));
                    victim.arrowHitTimer = 0;
                    victim.setAIMoveSpeed(0F);
                    if(!victim.isDead){
                        victim.setDead();
                        if(!victim.isDead){
                            victim.sendEndCombat();
                            victim.addedToChunk = false;
                            victim.getEntityWorld().removeEntity(victim);
                            if(!victim.isDead){
                                victim.move(MoverType.PISTON,0,Double.NaN,0);
                                victim.move(MoverType.SELF,0,Double.NaN,0);
                                victim.move(MoverType.PLAYER,0,Double.NaN,0);
                                victim.isDead = true;
                            }
                        }
                    }
                    Entity lighting = new EntityLightningBolt(p_onItemUse_2_,entity.posX,entity.posY,entity.posZ,true);
                    p_onItemUse_2_.spawnEntity(lighting);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return ModItems.COSMIC_RARITY;
    }
    @Override
    public Item setMaxDamage(int p_setMaxDamage_1_) {
        return this;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        //super.setDamage(stack, 0);
    }

    @Override
    @SideOnly (Side.CLIENT)
    public TextureAtlasSprite getMaskTexture(ItemStack stack, EntityLivingBase player) {
        return AvaritiaTextures.INFINITY_SWORD_MASK;
    }

    @Override
    @SideOnly (Side.CLIENT)
    public float getMaskOpacity(ItemStack stack, EntityLivingBase player) {
        return 1.0f;
    }

    //@SideOnly (Side.CLIENT)
    //@Override
    //public void registerIcons(IIconRegister ir) {
    //    super.registerIcons(ir);
    //    this.cosmicMask = ir.registerIcon("avaritia:infinity_sword_mask");
    //    this.pommel = ir.registerIcon("avaritia:infinity_sword_pommel");
    //}

    //@Override
    //public IIcon getIcon(ItemStack stack, int pass) {
    //    if (pass == 1) {
    //        return this.pommel;
    //    }
    //    return super.getIcon(stack, pass);
    // }

    //@SideOnly (Side.CLIENT)
    //@Override
    //public boolean requiresMultipleRenderPasses() {
    //    return true;
    //}

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityImmortalItem(world, location, itemstack);
    }

    @Override
    @SideOnly (Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack p_onDroppedByPlayer_1_, EntityPlayer p_onDroppedByPlayer_2_) {
        return false;
    }

    @Override
    @SideOnly (Side.CLIENT)
    public void registerModels() {
        ModelResourceLocation sword = new ModelResourceLocation("avaritia:tools", "type=infinity_sword");
        ModelLoader.registerItemVariants(ModItems.infinity_pickaxe, sword);
        IBakedModel wrapped = new CosmicItemRender(TransformUtils.DEFAULT_TOOL, modelRegistry -> modelRegistry.getObject(sword));
        ModelRegistryHelper.register(sword, wrapped);
        ModelLoader.setCustomMeshDefinition(ModItems.infinity_sword, (ItemStack stack) -> sword);
    }
}
