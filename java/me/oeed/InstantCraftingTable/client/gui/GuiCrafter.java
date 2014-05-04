package me.oeed.InstantCraftingTable.client.gui;

import java.lang.reflect.Field;
import me.oeed.InstantCraftingTable.CrafterSlot;
import me.oeed.InstantCraftingTable.InstantCraftingTable;
import me.oeed.InstantCraftingTable.client.gui.container.ContainerCrafter;
import me.oeed.InstantCraftingTable.helper.ConfigHelper;
import me.oeed.InstantCraftingTable.helper.CraftingHelper;
import me.oeed.InstantCraftingTable.helper.GuiHelper;
import me.oeed.InstantCraftingTable.helper.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


public class GuiCrafter extends GuiContainer{
	public static final ResourceLocation texture = new ResourceLocation(InstantCraftingTable.MODID.toLowerCase(), "textures/gui/crafter.png");
	public boolean isClientSideOnly;
	
	//used to 'mimic' a crafting table
	public ContainerWorkbench craftingTable;

    //Amount scrolled (0 = top, 1 = bottom)

    //True if the scrollbar is being dragged
    private boolean isScrolling;
	
    private boolean wasClicking;
    private GuiTextField searchField;
    
	public GuiCrafter(InventoryPlayer invPlayer, boolean isClientSideOnly, ContainerWorkbench craftingTable){
		super(new ContainerCrafter(invPlayer, isClientSideOnly, craftingTable));
		this.craftingTable = craftingTable;
		this.isClientSideOnly = isClientSideOnly;
        this.allowUserInput = true;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
		xSize = 195;
		ySize = 214;
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if(button.id == 0) {
			GuiHelper.toggleCrafter();
		} else if(button.id == 1){
			LogHelper.log("State: "+((GuiButtonCheckbox)button).isChecked);
			InstantCraftingTable.instance.configHelper.setValue(ConfigHelper.KEY_CRAFTINGTABLEDEFAULT, !((GuiButtonCheckbox)button).isChecked);
		}
	}
	
	@Override
	public void initGui(){
		super.initGui();
		buttonList.clear();
		buttonList.add(new GuiButtonCrafterToggle(0, this.guiLeft + 174, this.guiTop + 4, false));
		buttonList.add(new GuiButtonCheckbox(1, this.guiLeft + 8, this.guiTop + 198, I18n.getString("instantCraftingTable.defaultView"), !InstantCraftingTable.instance.configHelper.getValue(ConfigHelper.KEY_CRAFTINGTABLEDEFAULT)));
        Keyboard.enableRepeatEvents(true);
        this.searchField = new GuiTextField(this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRenderer.FONT_HEIGHT);
        this.searchField.setMaxStringLength(15);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setTextColor(16777215);
        this.searchField.setVisible(true);
        this.searchField.setCanLoseFocus(false);
        this.searchField.setFocused(true);
        this.searchField.setText("");
	}
	
	@Override
	public void drawGuiContainerBackgroundLayer(float f, int j, int i) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawTexturedModalRect(guiLeft + 175, (int) (guiTop + 18 + (73 * ((ContainerCrafter)this.inventorySlots).currentScroll)), 195 + (this.needsScrollBars() ? 0 : 12), 24, 12, 15);
        this.searchField.drawTextBox();
        GL11.glDisable(GL11.GL_LIGHTING);
        this.fontRenderer.drawString(I18n.getString("itemGroup.search"), guiLeft + 8, guiTop + 6, 4210752);
        GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3){
        boolean flag = Mouse.isButtonDown(0);
        int xStart = guiLeft + 174;
        int yStart = guiTop + 17;
        int xStop = xStart + 14;
        int yStop = yStart + 90;

        if (!this.wasClicking && flag && par1 >= xStart && par2 >= yStart && par1 < xStop && par2 < yStop){
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag){
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling){
        	((ContainerCrafter)this.inventorySlots).currentScroll = (par2 - yStart - 7.5F) / (yStop - yStart - 15.0F);

            if (((ContainerCrafter)this.inventorySlots).currentScroll < 0.0F)
            {
            	((ContainerCrafter)this.inventorySlots).currentScroll = 0.0F;
            }

            if (((ContainerCrafter)this.inventorySlots).currentScroll > 1.0F)
            {
            	((ContainerCrafter)this.inventorySlots).currentScroll = 1.0F;
            }

            ((ContainerCrafter)this.inventorySlots).scrollTo(((ContainerCrafter)this.inventorySlots).currentScroll);
        }

        super.drawScreen(par1, par2, par3);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }
	
	@Override
	protected void handleMouseClick(Slot slot, int slotIndex, int mouse, int shiftPressed){
		if(!(slot instanceof CrafterSlot)){
			CraftingHelper.craftingWindowClick(craftingTable, CraftingHelper.convertToCraftingSlot(craftingTable, slotIndex), mouse, shiftPressed, this.mc.thePlayer);
			return;
		}
        if (slot != null)
        {
            slotIndex = slot.slotNumber;
        }

        //this.mc.playerController.windowClick(this.inventorySlots.windowId, par2, par3, par4, this.mc.thePlayer);

        short short1 = this.mc.thePlayer.openContainer.getNextTransactionID(this.mc.thePlayer.inventory);

        ItemStack itemstack = this.mc.thePlayer.openContainer.slotClick(slotIndex, mouse, shiftPressed, this.mc.thePlayer);
        //this.mc.thePlayer.inventory.setItemStack(itemstack);
        if(!isClientSideOnly){
	        //if the server is taking part too let it know
	        try {   
		        Field field = PlayerControllerMP.class.getDeclaredField("netClientHandler");
		        field.setAccessible(true);
		        NetClientHandler netClientHandler = (NetClientHandler) field.get(this.mc.playerController);
		        netClientHandler.addToSendQueue(new Packet102WindowClick(this.inventorySlots.windowId, slotIndex, mouse, shiftPressed, itemstack, short1));
	        } catch (NoSuchFieldException e) {
	            throw new RuntimeException(e);
	        } catch (IllegalAccessException e) {
	            throw new RuntimeException(e);
	        }
        }
    }
	

    /**
     * Handles mouse input.
     */
    @Override
	public void handleMouseInput()
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0 && this.needsScrollBars())
        {
            int j = ((ContainerCrafter)this.inventorySlots).displayedRecipes.size() / 9 - 5 + 1;

            if (i > 0)
            {
                i = 1;
            }

            if (i < 0)
            {
                i = -1;
            }

            ((ContainerCrafter)this.inventorySlots).currentScroll = (float)(((ContainerCrafter)this.inventorySlots).currentScroll - (double)i / (double)j);

            if (((ContainerCrafter)this.inventorySlots).currentScroll < 0.0F)
            {
            	((ContainerCrafter)this.inventorySlots).currentScroll = 0.0F;
            }

            if (((ContainerCrafter)this.inventorySlots).currentScroll > 1.0F)
            {
            	((ContainerCrafter)this.inventorySlots).currentScroll = 1.0F;
            }

            ((ContainerCrafter)this.inventorySlots).scrollTo(((ContainerCrafter)this.inventorySlots).currentScroll);
        }
    }

    private boolean needsScrollBars(){
        return ((ContainerCrafter) inventorySlots).displayedRecipes.size() > 45;
    }
    
    @Override
	protected void keyTyped(char par1, int par2){
        if (!this.checkHotbarKeys(par2)){
            if (this.searchField.textboxKeyTyped(par1, par2)){
            	((ContainerCrafter) inventorySlots).update(searchField.getText().toLowerCase());
            }
            else{
                super.keyTyped(par1, par2);
            }
        }
    }



}
