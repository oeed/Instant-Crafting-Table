package me.oeed.InstantCraftingTable.client.gui;

import java.util.ArrayList;
import java.util.List;

import me.oeed.InstantCraftingTable.InstantCraftingTable;
import me.oeed.InstantCraftingTable.helper.ConfigHelper;
import me.oeed.InstantCraftingTable.helper.CraftingHelper;
import me.oeed.InstantCraftingTable.helper.GuiHelper;
import me.oeed.InstantCraftingTable.helper.LogHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class GuiCraftingCustom extends GuiContainer {

	public static final ResourceLocation texture = new ResourceLocation(InstantCraftingTable.MODID.toLowerCase(), "textures/gui/crafting_table.png");
    
    /**
	 * A replacement crafting interface that adds the check box and toggle button
	 */
    public GuiCraftingCustom(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5){
        super(new ContainerWorkbench(par1InventoryPlayer, par2World, par3, par4, par5));
        this.xSize = 176;
        this.ySize = 176;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2){
        this.fontRenderer.drawString(I18n.getString("container.crafting"), 28, 6, 4210752);
        this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 106 + 2, 4210752);
    }
	
	public void actionPerformed(GuiButton button){
		if(button.id == 0)
			GuiHelper.toggleCrafter();
		else if(button.id == 1){
			InstantCraftingTable.instance.configHelper.setValue(ConfigHelper.KEY_CRAFTINGTABLEDEFAULT, ((GuiButtonCheckbox)button).isChecked);
		}
	}
	
	public void initGui(){
		super.initGui();
		buttonList.clear();
		buttonList.add(new GuiButtonCrafterToggle(0, this.guiLeft + 158, this.guiTop + 5, true));
		buttonList.add(new GuiButtonCheckbox(1, this.guiLeft + 7, this.guiTop + 160, I18n.getString("instantCraftingTable.defaultView"), InstantCraftingTable.instance.configHelper.getValue(ConfigHelper.KEY_CRAFTINGTABLEDEFAULT)));
	}

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
    
}
