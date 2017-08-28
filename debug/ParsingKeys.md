Just realized something: the server appears to be stateless and must cache token Ids. 
So I assume token Ids map 1-1 to the combination of {word, all letters already guessed.}. 
Maybe the order of the guessed letters plays the role, maybe not, easy enough to check. 
Probably not, because that would be wasteful.

If that is true, then there is a 1-many relationship between the resulting word and all 
tokens that can be returned in the process of guessing the word. If so, it makes sense to 
cache all tokens, and once a familiar token is hit, that's instant success; otherwise, continue guessing.

It looks like this can be further improved by parsing the tokens. I would need to verify it, 
but it appears that all tokens in a given game look very similar. My guess is, there must 
be a 1-1 relationship between the word and some subset of characters in the key.  If that's true 
and if we know which subset (easy enough to find out by either heuristics or reading the Ruby code), 
then we simply need to cache about 235K values (one per word in the dictionary) and then, after 
playing the game long enough, we can instantaneously solve any game on first attempt, simply by 
parsing the key returned by "newGame" and looking it up. 

For example:

````
$ curl -X PUT -d token=eyJzb2x1dGlvbiI6InNwaXJvY2hldGljIiwiY29ycmVjdF9ndWVzc2VzIjpbImUiLCJyIl0sIndyb25nX2d1ZXNzZXMiOltdfQ==  -d letter=x http://hangman-api.herokuapp.com/hangman 
{"hangman":"___r___e___","correct":false,"token":"eyJzb2x1dGlvbiI6InNwaXJvY2hldGljIiwiY29ycmVjdF9ndWVzc2VzIjpbImUiLCJyIl0sIndyb25nX2d1ZXNzZXMiOlsieCJdfQ=="}
````

````
$ curl -X PUT -d token=eyJzb2x1dGlvbiI6InNwaXJvY2hldGljIiwiY29ycmVjdF9ndWVzc2VzIjpbImUiLCJyIl0sIndyb25nX2d1ZXNzZXMiOltdfQ==  -d letter=y http://hangman-api.herokuapp.com/hangman 
{"hangman":"___r___e___","correct":false,"token":"eyJzb2x1dGlvbiI6InNwaXJvY2hldGljIiwiY29ycmVjdF9ndWVzc2VzIjpbImUiLCJyIl0sIndyb25nX2d1ZXNzZXMiOlsieSJdfQ=="}
````

The last two have this in common:`eyJzb2x1dGlvbiI6InNwaXJvY2hldGljIiwiY29ycmVjdF9ndWVzc2VzIjpbImUiLCJyIl0sIndyb25nX2d1ZXNzZXMiOlsie`, differ in `JdfQ==` vs `SJdfQ==`
