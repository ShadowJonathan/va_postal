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
<br>
#### `/dist <"all"/"owners"> [PostOffice] [Expiration Days]`
Permissions: `postal.distr`
Distribute mail book to given Postal addresses [at `PostOffice`], defaults to sending it to *all* owners.
<br>
#### `/tlist`
Permissions: `postal.tlist`
Alias: `/tl`
Short for town-list, will present the player with a formatted, alphabetical list of towns when entered without parameters. The closest 3 towns, in order of distance, are also shown. If entered with enough characters to identify a particular town, the addresses of that town are listed.
<br>
#### `/alist <PostOffice>`
Permissions: `postal.alist`
Alias: `/al`
Short for address-list, will list the addresses of the closest town when entered without parameters. Like /tlist, it will list the addresses of a particular town if entered with enough characters to identify it. The two commands complement each other including details that the other doesn’t.
<br>
#### `/plist [Substring match]`
Permissions: `postal.plist`
Alias: `postal.plist`
Short for player-list, lists the closes 8 players, in order of distance when entered without parameters. Along with the listed player is the Postal address he/she is closest to with the compass heading required to get there. If entered with enough characters to complete a player name, Postal will list any Postal addresses or post offices owned by the player.
<br>
#### `/gps <PostOffice> [Address]`
Permissions: `postal.gps`
Alias: `/gpsp`
Locates your compass to `PostOffice/(Address @ PostOffice)`.
<br>
#### `/expedite <PostOffice> <address>`
Permissions: `postal.expedite`
Alias: `/ex`
Pushes the route queue forward so that the local postman at `PostOffice` will visit `address` next.
<br>
#### `/setcentral`
Permissions: `postal.setcentral`
Sets the central postoffice at the current location.
<br>
#### `/setlocal <new PostOffice>`
Permissions: `postal.setlocal`
Sets a new local post office at the current location.
<br>
#### `/setaddr [PostOffice] <address>`
Permissions: `postal.setaddr`
Sets a new address `address` at the current location linked to `PostOffice` _(is filled in automatically when it's within 500 blocks and no `PostOffice` argument was given)_
<br>
#### `/setroute [[PostOffice] <address>]`
Permissions: `postal.setroute`
Opens the route editor for `address` or the nearest within 15 blocks if not given.
<br>
#### `/setowner [<PostOffice> (or/and) [address]] <player>`
Permissions: `postal.owneraddr, postal.ownerlocal`
Subcommands:
```
/setaddr <PostOffice> <address> <player>
/setlocal <PostOffice> <player>
```
`/setowner` resolves in `/setaddr` or `/setlocal` first, depending on the arguments.
Both give the ownership of the postal object in question (`address` or `PostOffice`) to `player`
<br>
#### `/showroute [[PostOffice] <address>]`
Permissions: `postal.showroute`
Alias: `/sr`
Shows the route of `address` or the nearest one in 15 blocks (if no arguments given) to the player for a short amount of time.
<br>
#### `/openX` and `/closeX`
Permissions:
```
postal.openlocal,
postal.closelocal,
postal.openaddr,
postal.closeaddr
```
Commands:
```
/openlocal <PostOffice>
/closelocal <PostOffice>
/openaddr <PostOffice> <address>
/closeaddr <PostOffice> <address>
```
Opens or Closes the postal object, a closed address means no post can be delivered from or to that address, a closed PostOffice means no post can be delivered to *any* of the addresses attached to it, a closed address/postoffice can be identified by it's red-titled sign.
<br>
#### `/deletelocal <PostOffice>` and `/deleteaddr <PostOffice> <address>`
Permissions: `postal.deletelocal, postal.deleteaddr`
Deletes the mentioned address from the database, but the chests and signs will stay, and be protected till a restart of the postal service.