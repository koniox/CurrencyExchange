# CurrencyExchange
Simple currency exchange app for recruitment purposes written with use of Spring Boot
## Exposed endpoints with exemplary body

POST /api/v1/account - to create an account
{
    "firstName" :"John",
    "lastName"  : "Doe",
    "plnBalance": "10"
}

GET /api/v1/account/{id} - to retrieve specific account data

PUT /api/v1/account/{id}/to-usd - to exchange specific account's PLN balance to USD
{
  "amountToExchange": 100
}

PUT /api/v1/account/{id}/to-pln - to exchange specific account's USD balance to PLN
{
  "amountToExchange": 100
}
### Requirements of the app (in polish)![261987093_640432830704010_7545800500810988730_n](https://user-images.githubusercontent.com/35576147/165088772-e3e5ecfc-fcbb-40d2-b82a-94dbb9daec5c.png)
