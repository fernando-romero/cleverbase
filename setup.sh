#!/bin/bash

# Delete all users:

curl $1/users -X DELETE

# Delete all secrets:

curl $1/secrets -X DELETE

# Create users:

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"joris","password":"tinbergen","mate":"remco","isSuper":true}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"remco","password":"vanwijk","mate":"joris","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"valeria","password":"boshnakova","mate":"rene","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"rene","password":"kleizen","mate":"valeria","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"lukas","password":"spee","mate":"vincent","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"vincent","password":"dehaan","mate":"lukas","isSuper":false}' \
	| python -m json.tool

# Super user login

curl $1/login -X POST -u joris:tinbergen | python -m json.tool

# Non-super user login

curl $1/login -X POST -u remco:vanwijk -i

# Show users

curl localhost:8080/users -u joris:tinbergen | python -m json.tool

# Create secret with logged in user

curl $1/secrets -X POST \
	-u joris:tinbergen \
	-H "Content-Type: application/json" \
	-d '{"id":"secret1","text":"this is secret 1"}' \
	| python -m json.tool

# Create secret with logged out user

curl $1/secrets -X POST \
	-u remco:vanwijk \
	-H "Content-Type: application/json" \
	-d '{"id":"secret1","text":"this is secret 1"}' -i

# Get own secrets

curl $1/secrets -u joris:tinbergen | python -m json.tool

# Share secret

curl $1/secrets -X PUT \
	-u joris:tinbergen \
	-H "Content-Type: application/json" \
	-d '{"id":"secret1","sharedWith":["remco"]}' \
	| python -m json.tool

# Get secrets shared with user

curl $1/shared -u remco:vanwijk | python -m json.tool



