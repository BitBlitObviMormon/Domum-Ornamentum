package com.ldtteam.domumornamentum.datagen.door.fancy;

import com.ldtteam.domumornamentum.block.ModBlocks;
import com.ldtteam.domumornamentum.block.decorative.FancyDoorBlock;
import com.ldtteam.domumornamentum.block.types.FancyDoorType;
import com.ldtteam.domumornamentum.datagen.MateriallyTexturedModelBuilder;
import com.ldtteam.domumornamentum.util.Constants;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class FancyDoorsBlockStateProvider extends BlockStateProvider
{
    public FancyDoorsBlockStateProvider(final DataGenerator gen, final ExistingFileHelper exFileHelper)
    {
        super(gen, Constants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        FancyDoorBlock(ModBlocks.getInstance().getFancyDoor(), FancyDoorType::getSerializedName);
    }

    private void FancyDoorBlock(FancyDoorBlock block, Function<FancyDoorType, String> baseName)
    {
        Function<FancyDoorType, ModelFile> bottomLeft = createModel(baseName, "bottom_left");
        Function<FancyDoorType, ModelFile> bottomLeftOpen = createModel(baseName, "bottom_left_open");
        Function<FancyDoorType, ModelFile> bottomRight = createModel(baseName, "bottom_right");
        Function<FancyDoorType, ModelFile> bottomRightOpen = createModel(baseName, "bottom_right_open");
        Function<FancyDoorType, ModelFile> topLeft = createModel(baseName, "top_left");
        Function<FancyDoorType, ModelFile> topLeftOpen = createModel(baseName, "top_left_open");
        Function<FancyDoorType, ModelFile> topRight = createModel(baseName, "top_right");
        Function<FancyDoorType, ModelFile> topRightOpen = createModel(baseName, "top_right_open");
        FancyDoorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
    }

    private Function<FancyDoorType, ModelFile> createModel(Function<FancyDoorType, String> baseName, String stateDescription)
    {
        return type -> models()
                               .withExistingParent("block/doors/fancy/door_" + baseName.apply(type) + "_" + stateDescription, new ResourceLocation(Constants.MOD_ID, "block/doors/fancy/door_" + baseName.apply(type) + "_" + stateDescription + "_spec"))
                               .customLoader(MateriallyTexturedModelBuilder::new)
                               .end();
    }

    public void FancyDoorBlock(FancyDoorBlock block, Function<FancyDoorType, ModelFile> bottomLeft, Function<FancyDoorType, ModelFile> bottomLeftOpen, Function<FancyDoorType, ModelFile> bottomRight, Function<FancyDoorType, ModelFile> bottomRightOpen, Function<FancyDoorType, ModelFile> topLeft, Function<FancyDoorType, ModelFile> topLeftOpen, Function<FancyDoorType, ModelFile> topRight, Function<FancyDoorType, ModelFile> topRightOpen)
    {
        //Generate all block models from the variant
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = ((int) state.getValue(FancyDoorBlock.FACING).toYRot()) + 90;
            boolean right = state.getValue(FancyDoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.getValue(FancyDoorBlock.OPEN);
            boolean lower = state.getValue(FancyDoorBlock.HALF) == DoubleBlockHalf.LOWER;
            FancyDoorType type = state.getValue(FancyDoorBlock.TYPE);
            if (open)
            {
                yRot += 90;
            }
            if (right && open)
            {
                yRot += 180;
            }
            yRot %= 360;

            ModelFile model = null;
            if (lower && right && open)
            {
                model = bottomRightOpen.apply(type);
            } else if (lower && !right && open)
            {
                model = bottomLeftOpen.apply(type);
            }
            if (lower && right && !open)
            {
                model = bottomRight.apply(type);
            } else if (lower && !right && !open)
            {
                model = bottomLeft.apply(type);
            }
            if (!lower && right && open)
            {
                model = topRightOpen.apply(type);
            } else if (!lower && !right && open)
            {
                model = topLeftOpen.apply(type);
            }
            if (!lower && right && !open)
            {
                model = topRight.apply(type);
            } else if (!lower && !right && !open)
            {
                model = topLeft.apply(type);
            }

            return ConfiguredModel.builder().modelFile(model)
                                  .rotationY(yRot)
                                  .build();
        }, FancyDoorBlock.POWERED);


        //Next up generate a single spec model with a variant aware resolver
        final ItemModelBuilder overarchingSpecBuilder = itemModels().getBuilder(ModBlocks.getInstance().getFancyDoor().getRegistryName().getPath() + "_spec");
        buildItemModelDisplayTransforms(overarchingSpecBuilder.transforms());
        FancyDoorType[] values = FancyDoorType.values();
        for (int i = 0; i < values.length; i++)
        {
            final FancyDoorType value = values[i];
            overarchingSpecBuilder.override()
                                  .predicate(new ResourceLocation(Constants.DOOR_MODEL_OVERRIDE), i)
                                  .model(models().getExistingFile(new ResourceLocation(Constants.MOD_ID + ":item/doors/fancy/door_" + value.getSerializedName() + "_spec")))
                                  .end();
        }


        //Now generate the single over arching item model which is loaded by the material manager.
        final ItemModelBuilder rootBuilder = itemModels().getBuilder(ModBlocks.getInstance().getFancyDoor().getRegistryName().getPath());
        buildItemModelDisplayTransforms(rootBuilder.transforms());
        rootBuilder.customLoader(MateriallyTexturedModelBuilder::new);
        rootBuilder.parent(overarchingSpecBuilder);
    }

    private void buildItemModelDisplayTransforms(final ModelBuilder<ItemModelBuilder>.TransformsBuilder transformsBuilder)
    {
        transformsBuilder.transform(ItemTransforms.TransformType.GUI)
                         .rotation(30, 225, 0)
                         .translation(-2f, -4f, 0)
                         .scale(0.45f, 0.45f, 0.45f)
                         .end()
                         .transform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
                         .rotation(75, 45, 0)
                         .translation(0, 2.5f, 0)
                         .scale(0.375f, 0.375f, 0.375f)
                         .end()
                         .transform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                         .rotation(75, 45, 0)
                         .translation(0, 2.5f, 0)
                         .scale(0.375f, 0.375f, 0.375f)
                         .end()
                         .transform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
                         .rotation(0, 225, 0)
                         .scale(0.4f, 0.4f, 0.4f)
                         .end()
                         .transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
                         .rotation(0, 225, 0)
                         .scale(0.4f, 0.4f, 0.4f)
                         .end()
                         .transform(ItemTransforms.TransformType.GROUND)
                         .translation(0, 3, 0)
                         .scale(0.25f, 0.25f, 0.25f)
                         .end()
                         .transform(ItemTransforms.TransformType.FIXED)
                         .scale(0.5f, 0.5f, 0.5f)
                         .end()
                         .transform(ItemTransforms.TransformType.HEAD)
                         .scale(1.03f, 1.03f, 1.03f)
                         .end();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Doors BlockStates Provider";
    }
}
