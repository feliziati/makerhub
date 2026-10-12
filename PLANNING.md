* The app will use Clean architecture with a data-domain-presentation layer pattern.
* Since the app involves big datasets, it will probably not be a good idea to transform the data before saving it. Keep the data as-is in the data layer and transform it in the domain layer when querying it to the UI. Each data entity will have 3 data classes: the raw API response object, an entity class for Room, and a model class in the domain/UI layer. The data could be preserved in its original format in consideration for future data exports via the app. 
* Data validation and cleaning:

  * Validate any missing data fields in the domain layer before saving to the data layer to keep track of missing fields for more comprehensive reporting. Data conflicts will be handled in the Dao by checking the latest “updatedAt” field as well as valid transitions of the state machine
  * Since the ISO string formatted Date field can contain errors, include validate dates before saving
  * Standardize the ISO string to UNIX time for faster comparison
  * When something is wrong with the data, throw an exception in the lower data/domain layer functions with the message for reporting purposes and catch them in the domain layer. Ensure that all exceptions are caught safely in the domain layer.
* Create an “IResult” interface for the UI to propagate success/errors from the domain uniformly. This interface can be reused by any function of the app.
* Define UseCases separately per usable feature of the app: Import, Checkout, Return, Mark as Lost, Query item list, etc., and inject them in ViewModels where they are used.
* Use LiveData for data persistence in the UI layer.


What I cut and why:

* Fragment/Nav controller UI - To keep the app simple. The app has only a few screens with few transitions and there is no need to share extensive amounts of data between different screens.
* Optimized queries and data transformations - No time to test how much it will improve performance. Could be improved by utilizing coroutine dispatchers to consider larger datasets
* More comprehensive unit testing - Rapid decision making/refactoring and scaling the business logic made me skip unit tests in favor of testing whole classes/features at a time.