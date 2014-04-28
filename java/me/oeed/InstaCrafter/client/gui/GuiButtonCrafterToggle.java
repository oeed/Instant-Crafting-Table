package me.oeed.InstaCrafter.client.gui;

import me.oeed.InstaCrafter.InstaCrafter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonCrafterToggle extends GuiButton{
	public static final ResourceLocation texture = new ResourceLocation(InstaCrafter.MODID.toLowerCase(), "textures/gui/crafter.png");
	
	public static final int CrafterWidth = 14;
	public static final int CraftingWidth = 13;
	
	public boolean isOnCraftingTable;

	 public GuiButtonCrafterToggle(int id, int x, int y, boolean _isOnCraftingTable){
		super(id, x, y, CrafterWidth, 12, "");
		if(_isOnCraftingTable)
			this.width = CraftingWidth;
		this.isOnCraftingTable = _isOnCraftingTable;
	 }

    public void drawButton(Minecraft minecraft, int par2, int par3){
        if (this.drawButton){
    		GL11.glColor4f(1F, 1F, 1F, 1F);
    		minecraft.renderEngine.bindTexture(texture);
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int u = 195;
            int v = this.getHoverState(this.field_82253_i) - 1;
            if(isOnCraftingTable)
            	u += CrafterWidth;
            this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v * 12, this.width, this.height);
        }
    }
	
}
