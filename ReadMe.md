## Welcome to Volcano Island Reservation API

### This booking system has the following endpoints:
- /reservations
  - Used to create and search for reservations.
- reservations/{bookingReferenceId}
    - Used to update(modify) or delete an existing reservation with a unique booking reference identifier.
    
### Specifications of this API
- Built using Java11/Spring 
- Single SQL database which is hosted remotely

### Sample Requests
- To create a reservation, users can follow the below sample:

    ```
    curl -XPOST -v 
    "http://localhost:PORTNUMBER/reservations" 
    -H 'Content-Type: application/json' 
    -d '{
        "lastName":"Leech", 
        "firstName":"Marie", 
        "email":"marie.leech98@gmail.com",
        "checkInDate":"2022-05-20'T'00:00:00",
        "checkOutDate":"2022-05-23'T'00:00:00"
    }'
    ```
    
    If the reservation is successful, then the user will be returned with the following message, which also includes their unique booking reference id: 
    
    `Reservation successful! Here is your booking reference number:BOOKING-REFERENCE`
    
    If their reservation is unsuccessful due to several possible reasons:
    - dates are no longer available
    - dates to not match business rules
    - invalid date format
    - missing personal information
    
    Then the user will receive the appropriate error message explaining why. 

- To update their reservation, the user can follow the below example:
     ```
    curl -XPUT -v 
    "http://localhost:PORTNUMBER/reservations/BOOKING-REFERENCE" 
    -H 'Content-Type: application/json' 
    -d '{
        "lastName":"Leech",
        "firstName":"Marie",
        "email":"marie.leech98@gmail.com",
        "checkInDate":"2022-05-20'T'00:00:00",
        "checkOutDate":"2022-05-23'T'00:00:00"
    }'
    ```

  If the reservation update is successful, then the user will be returned with the following message, which also includes their unique booking reference id:

  `Update of reservation successful!`

  If their reservation is unsuccessful due to several possible reasons:
    - Invalid booking reference
    - Modified dates are not available

  Then the user will receive the appropriate error message explaining why. 

- To delete their reservation, the user can follow the below example:
  ```
  curl -XDELETE -v
  "http://localhost:PORTNUMBER/reservations/BOOKING-REFERENCE"
  -H 'Content-Type: application/json'
    ```

  If the reservation update is successful, then the user will be returned with the following message, which also includes their unique booking reference id:

  `Your reservation has successfully been deleted.`

  If their reservation is unsuccessful due to an invalid booking reference, then the user will receive the appropriate error message explaining why. 

- If the user wishes to find available reservation times, they can follow the below example:
    ```
    curl -XGET -v
    "http://localhost:PORTNUMBER/reservations"
    -H 'Content-Type: application/json'
    -d '{
    "checkInDate":"2022-05-20'T'00:00:00",
    "checkOutDate":"2022-05-23'T'00:00:00"
    }'
    ```
    Note that the request body is optional, as the default date range would be one month from the current date.
    
    If there are availabilities, a list of possible dates will be returned to the user.
    If there are no dates available, an appropriate message will be returned.
