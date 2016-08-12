package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.gui.grid.GuiGrid;
import refinedstorage.gui.grid.stack.ClientStackItem;

import java.util.ArrayList;
import java.util.List;

public class MessageGridUpdate implements IMessage, IMessageHandler<MessageGridUpdate, IMessage> {
    private INetworkMaster network;
    private List<ClientStackItem> items = new ArrayList<>();

    public MessageGridUpdate() {
    }

    public MessageGridUpdate(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int items = buf.readInt();

        for (int i = 0; i < items; ++i) {
            this.items.add(new ClientStackItem(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(network.getItemStorage().getStacks().size());

        for (ItemStack stack : network.getItemStorage().getStacks()) {
            ClientStackItem.write(buf, network, stack);
        }
    }

    @Override
    public IMessage onMessage(MessageGridUpdate message, MessageContext ctx) {
        GuiGrid.ITEMS.clear();

        for (ClientStackItem item : message.items) {
            GuiGrid.ITEMS.put(item.getStack().getItem(), item);
        }

        GuiGrid.markForSorting();

        return null;
    }
}
