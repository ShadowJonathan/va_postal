#### `/postal`
Permissions: `none`
Displays a menu with commands available to the player.
<br>
#### `/postal <start/stop/admin/chests/talk/quiet/speed>`
Permissions: `postal.admin`
Administrator commands for postal:
##### `speed <0.5 - 2.0>`
NPC walking speed factor
##### `admin`
Disables all security for 60 seconds, **_for all users_**
##### `bypass`
Disables all security for 5 minutes, for all admins.
##### `start`
Starts postal, initiates all queues, and spawns postmen if necessary.
##### `stop`
Stops postal, pauses all queues, and leaves the postmen for the citizens API to clean up.
##### `restart`
Restarts postal.
##### `chests`
Lists all chests and locations _(only works when running)_
<br>
#### `/go [PostOffice] [Address]` _`(No address defaults to central)`_
Permissions: `postal.gotocentral, postal.gotolocal, postal.gotoaddr`
Subcommands:
```
/gotocentral
/gotolocal <PostOffice>
/gotoaddr <PostOffice> <Address>
```
Teleports the player to the given address, postoffice, or central office.
<br>
#### `/addr <PostOffice> <address> [player]`
Permissions: `postal.addr`
Address a signed book in your hand _(to `player`)_ at `PostOffice:Address`
<br>
#### `/att [player]`
Permissions: `postal.att`
Re-Addresses the unprocessed book in your hand to `player`
No argument defaults the addressed to "`[resident]`"
<br>
#### `/package <PostOffice> <address> [player]`
Permissions: `postal.package`
Alias: `/pk`
Package a chest full of goods, the player must stand near the chest, with it having no signs near or on.
Otherwise, this command is used in the same was as `/addr`
<br>
#### `/cod <price>`
Permissions: `postal.cod`
Needs: `Economy`
COD = Cash On Delivery
When holding a shippable item, issuing this command will charge a configured amount from the player's account to stamp the item with a price for the receiver to pay when `/accept`-ing the package.
Read more on the [Economy page on bukkit](https://dev.bukkit.org/projects/postal-forwarded/pages/economy#title-6)
<br>
#### `/accept & /refuse`
Permissions: `postal.accept, postal.refuse`
`/accept` will accept the package and the attached COD payments, if the player can't pay these, the command is denied.
It will place the package (chest) in front of the player.
`/refuse` will refuse the package for the player, and place it back at it's origin, it will display an error when it cannot place it back.

#### `/dist <all/owners> [PostOffice] [Expiration Days]`
Permissions: `postal.distr`
Distribute mail book to given Postal addresses