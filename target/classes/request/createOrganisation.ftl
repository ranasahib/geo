{
  "transaction": [
    {
      "insert": [
        {
          "id": "Organization",
          "objectType": "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Organization",
          "status": "urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted",
          "name": [
            {
              "lang": "en",
              "charset": "UTF-8",
              "value": "${name}"
            }
          ],
          "description": [
            {
              "lang": "en",
              "charset": "UTF-8",
              "value": "${description}"
            }
          ],
          "externalIdentifiers": [
            
          ],
          "addresses": [
            {
              "city": "${addresses.city}",
              "country": "${addresses.country}",
              "postalCode": "${addresses.postalCode}",
              "stateOrProvince": "${addresses.stateOrProvince}",
              "street": "${addresses.street}",
              "streetNumber": "${addresses.streetNumber}"
            }
          ],
          "emailAddresses": [
            {
              "address": "${emailAddresses}"
            }
          ],
          "telephoneNumbers": [
            {
              "countryCode": "${countryCode}",
              "number": "${number}",
              "phoneType": "${phoneType}"
            }
          ]
        }
      ]
    }
  ]
}