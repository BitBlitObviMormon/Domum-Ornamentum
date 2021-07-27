package com.ldtteam.domumornamentum.block;

import com.ldtteam.domumornamentum.block.interfaces.IDOBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class AbstractBlockPane<B extends AbstractBlockPane<B>> extends IronBarsBlock implements IDOBlock<B>
{
    protected AbstractBlockPane(final Properties properties)
    {
        super(properties);
    }

    /**
     * Registery block at gameregistry.
     *
     * @param registry the registry to use.
     * @return the block itself.
     */
    @Override
    @SuppressWarnings("unchecked")
    public B registerBlock(final IForgeRegistry<Block> registry)
    {
        registry.register(this);
        return (B) this;
    }

    @Override
    public void registerItemBlock(final IForgeRegistry<Item> registry, final Item.Properties properties)
    {
        registry.register((new BlockItem(this, properties)).setRegistryName(this.getRegistryName()));
    }
}