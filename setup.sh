#!/bin/bash

# Delete all users:

curl $1/users -X DELETE

# Delete all secrets:

curl $1/secrets -X DELETE

# Create users:

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"fernando","password":"romero","mate":"martijn","isSuper":true}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"joris","password":"tinbergen","mate":"fernando","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"remco","password":"vanwijk","mate":"joris","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"valeria","password":"boshnakova","mate":"remco","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"rene","password":"kleizen","mate":"valeria","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"lukas","password":"spee","mate":"rene","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"vincent","password":"dehaan","mate":"lukas","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"bas","password":"dekwant","mate":"vincent","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"lenno","password":"toet","mate":"bas","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"sander","password":"dijkhuis","mate":"lenno","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"kirill","password":"korneev","mate":"sander","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"david ","password":"lorie","mate":"kirill","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"raul","password":"maduro","mate":"david","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"willem","password":"vermeer","mate":"raul","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"sjaak","password":"vandenberg","mate":"willem","isSuper":false}' \
	| python -m json.tool

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"martijn","password":"smeets","mate":"sjaak","isSuper":false}' \
	| python -m json.tool
