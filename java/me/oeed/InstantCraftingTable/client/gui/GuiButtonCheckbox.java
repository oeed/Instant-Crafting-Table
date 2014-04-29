package me.oeed.InstantCraftingTable.client.gui;

import me.oeed.InstantCraftingTable.InstantCraftingTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonCheckbox extends GuiButton {
	public static final ResourceLocation texture = new ResourceLocation(InstantCraftingTable.MODID.toLowerCase(), "textures/gui/crafter.png");
	
	public boolean isChecked;
	
	 public GuiButtonCheckbox(int id, int x, int y, String display, boolean checked){
		super(id, x, y, 12, 11, display);
		this.isChecked = checked;
	 }

    public void drawButton(Minecraft minecraft, int par2, int par3){
        if (this.drawButton){
    		GL11.glColor4f(1F, 1F, 1F, 1F);
    		minecraft.renderEngine.bindTexture(texture);
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int u = 195;
            int v = 39;
            if(isChecked)
            	u += 12;
            this.drawTexturedModalRect(this.xPosition, this.yPosition, u, v, this.width, this.height);
            //I18n.getString("itemGroup.search")
            GL11.glDisable(GL11.GL_LIGHTING);
            minecraft.fontRenderer.drawString(displayString, this.xPosition + this.width + 3, this.yPosition + 2, 4210752);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }
    
    public boolean mousePressed(Minecraft minecraft, int x, int y){
    	if(super.mousePressed(minecraft, x, y)){
    		isChecked = !isChecked;
    		return true;
    	}
    	else{
    		return false;
    	}
    }
	
}
