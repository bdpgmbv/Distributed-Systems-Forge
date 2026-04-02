# 01f — GraphQL Demo (Query, Mutation, GraphiQL)

## Run
```bash
./gradlew bootRun
```

## Test in Browser
Open: http://localhost:8080/graphiql

Try these queries:
```graphql
{ user(id: 1) { name email } }
{ users { id name } }
{ searchUsers(name: "alice") { name email } }
mutation { createUser(name: "New", email: "new@t.com") { id name } }
```

## Test with curl
```bash
curl -X POST localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query":"{ user(id: 1) { name email } }"}'

curl -X POST localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query":"{ users { id name } }"}'

curl -X POST localhost:8080/graphql -H "Content-Type: application/json" \
  -d '{"query":"mutation { createUser(name: \"GQL\", email: \"g@t.com\") { id name } }"}'
```

## Key Insight
Client picks EXACTLY what fields it wants. No over-fetching.
Same endpoint, infinite flexibility.
