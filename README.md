# Game Store API

A demo backend API for an online game store.

## API Endpoints

1. GET /game
    Returns all games in the database in the JSON format.
    Arguments:
    - name  
        A string that each result's name must contain
    - studio
        A string that each result's studio must contain
    - date
        A string that each result's release date must contain

2. POST /game/delete
    Deletes a game entry from the database with the provided uid.

3. POST /game/add
    Adds a new game entry to the database with the attributes in arguments.

4. POST /game/update
    Updates a game entry in the database with the attributes in POST arguments.

5. POST /key/valid
    Returns if the key in POST arguments is valid.

## Notes

- I had trouble with Java setup, thus two jar files are bundled.
- The games table has very little columns, but that's not important and can be easily changed.