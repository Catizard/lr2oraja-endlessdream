# LR2oraja \~Endless Dream\~ ArenaEx

This is an unauthorized, hand-crafted [LR2ArenaEx](https://github.com/SayakaIsBaka/LR2ArenaEx) port. And based on Endless Dream's main branch(`0.3.0`).

In short, this repo brings the functionality:

- As a client join other LR2ArenaEx's server session
- Can play with LR2 users
- Host as a server

To build this branch, follow the instruction from upstream's README and rememeber to upgrade the jdk version to 17 at least.

## Development

Need some time to polish this project...

- [x] Porting whole server side
    - [ ] The server side is far from usable, need some time to test 
- [ ] Play scene
    - [x] Auto select the host's song(see note below)
    - [x] A/AA/AAA limits are miscalculated
    - [x] The height of graph menu should be adjusted. I don't know why there's a horizontal bar
    - [x] Kick users/Give host to other users are not implemented yet
    - [ ] Hijack the IR display to render lobby result instead
    - [ ] The play result should be reversed
    - [ ] Cancel event seems not sending
    - [ ] Hijack the pace maker to see lobby's status(optional)
- [ ] Settings
    - [ ] Items are not implemented
    - [ ] Random seed is not implemented
    - [ ] Option & Gauge are not implemented

> [!note]
> The auto-select feature is currently implemented in an awkward way: for every non-host user, selecting any chart would equally choose the host song if it exists.
> So basically, once host setup the song, other users can pick any song and this will pick the same song from host if possible
> There's a risk of crashing if we implement the direct state change instead, still need some time to do the task

## Other things you might want ask

Q: How do I pick a song/get ready?  
A: Just launch the bms as usual. If you are the lobby's host, then the song would be picked for the lobby. For other users, selecting the same song with host and you are ready.

Q: What's the current progress?  
A: Only the basic functionality of client side has been implemented. Server side 0%.

Q: Does this branch has any relationship with lr2oraja arena or something similar?  
A: No. This branch is only a code port result from LR2ArenaEx.

Q: Why this code is a branch not a standalone repo?(outdated)
A: It's a standalone repo now because I find that github action is only triggered on main branch...

Q: Does this branch allows IR connections?  
A: Technically yes but this branch is based on dev branch of Endless Dream. So I think I'll disallow the connection in the near future.
