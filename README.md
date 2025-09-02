# LR2oraja \~Endless Dream\~ ArenaEx

This is an unauthorized, hand-crafted [LR2ArenaEx](https://github.com/SayakaIsBaka/LR2ArenaEx) port. And based on Endless Dream's main branch(`0.3.0`).

In short, this branch brings the functionality:

- As a client join other LR2ArenaEx's server session
- Can play with LR2 users

To build this branch, follow the instruction from main branch's README file but upgrade the jdk version to 17 at least.

## Development

Need some time to polish this project...

- [ ] Porting whole server side
- [ ] Play scene
    - [ ] Auto select the host's song
    - [ ] A/AA/AAA limits are miscalculated
    - [ ] The height of graph menu should be adjusted. I don't know why there's a horizontal bar
    - [ ] Kick users/Give host to other users are not implemented yet
    - [ ] Hijack the IR display to render lobby result instead
- [ ] Settings
    - [ ] Items are not implemented
    - [ ] Random seed is not implemented
    - [ ] Option & Gauge are not implemented

## Other things you might want ask

Q: How do I pick a song/get ready?  
A: Just launch the bms as usual. If you are the lobby's host, then the song would be picked for the lobby. For other users, selecting the same song with host and you are ready.

Q: What's the current progress?  
A: Only the basic functionality of client side has been implemented. Server side 0%.

Q: Does this branch has any relationship with lr2oraja arena or something similar?  
A: No. This branch is only a code port result from LR2ArenaEx.

Q: Why this code is a branch not a standalone repo?  
A: To hard to think of a name for the repo...I don't want something like lr2oraja-endlessdream-arenaex

Q: Does this branch allows IR connections?  
A: Technically yes but this branch is based on dev branch of Endless Dream. So I think I'll disallow the connection in the near future.
