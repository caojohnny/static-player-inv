# static-player-inv

Heavy-weight solution to https://www.spigotmc.org/threads/keep-items-in-crafting-slots.323372/

Here is another thread I found interesting enough to make a project out of. Minecraft's protocol basically doesn't suppport opening a player's own inventory on the server side, so getting when the player opens the player inventory is near impossible as it doesn't require any interaction at all with the server. This user wanted an item to remain inside of their crafting matrix, but as the server doesn't know when the inventory closes or opens, there is no way to manage that.

It is however posisble to know when a player closes the inventory (sort of) and so you can clear the matrix before it closes so the player can't get the item into their inventory. To make sure the item stays in the matrix, just keep setting the item as there is no way it can be retrieved without opening the inventory, in which case the event can be cancelled like normal.
