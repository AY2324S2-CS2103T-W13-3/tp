

# Rainbow Dragon Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.


### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.


### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.


### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)


### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add function

The new add function allows user to add new contacts to the address book.
<br>
Users can now choose to add tags to the contact upon addition of new contacts by running the command as `add n/name t/tag`.
<br>
Users can also add multiple tags to a person upon addition of a new contact by running `add n/name t/tag1 t/tag2`.
<br>
Users can also assign new contact categories upon addition by running `add n/name t/tag c/cat d/description`, where `cat` is the category associated
with the new contact and `description` is the category information that is assigned to the new contact.
<br>
Users can also assign multiple categorical information to the new contact added by running `add n/name t/tag c/cat1, cat2, cat3 d/d1, d2, d3`,
where `cat1` corresponds to `d1` and `cat2` corresponds to `d2` and so on. Users can definitely add more than 3 categories with descriptions in one addition.
<br>
The sequence diagram below illustrates how the add function can be used.

<puml src="diagrams/AddSequenceDiagram.puml" alt="Add Command Sequence Diagram" />
<br>


### AddCategory function

The new addCategory function allows user to be able to add any category to a person.
<br>
The sequence diagram below illustrates the interaction within the `Logic` component, taking `execute("addCategory 1 c/class d/warrior")` API call as an example.

<puml src="diagrams/AddCategorySequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `addCategory 1 c/class d/warrior` Command" />
<br>

#### 1. How the feature is implemented

New entries are added to Persons through the `AddCategoryCommand` and `AddCategoryCommandParser` classes.
<br>
The `AddCategoryCommand` class handles the logic of adding a person's details, such as category and description.
<br>
The execute method in `AddCategoryCommand` performs the actual update, ensuring that the edited person does not duplicate existing categories.
<br>
The `AddCategoryCommandParser` class parses user input into an `AddCategoryCommand` object. It validates the input and extracts the necessary information
(like index, category and description) to instantiate a `AddCategoryCommand`.
<br>

#### 2. Why it is implemented that way

Entry requiring only two Strings meant that the type of entries that can be created by the user is limitless, suitable for an application that inputs potential characteristics
from games. As such, all numbers and characters are allowed. However, due to deleteCategory requiring the category name, it was decided that categories be unique per person.
<br>
It was required that index be specified as Name can be non-unique in address book.
<br>
Splitting the responsibilities of interpreting the user input and converting it into an output into a Parser class and Command class allows for ease in maintaining the code.
<br>

#### 3. Alternatives considered

Given our initial vision of a customisable field option for the address book persons, there was not really much of an alternative as gaming contacts can vary quite widely. It would not make sense to have compulsory fields except for name since many things like phone, address and email may be unknown to the user for online or gaming contacts otherwise. This way, things like gaming information can be captured with no restrictions.
<br>


### Delete function

The delete function allows users to remove existing contacts from the address book.
<br>
Users can delete a contact by specifying the contact's index in the displayed list by running the command `delete INDEX`. For example, `delete 3` will remove the third contact from the address book.
<br>
The sequence diagram below illustrates how the delete function can be used.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />
<br>


### DeleteCategory function

The command `deleteCategory INDEX c/CATEGORY` allows users to delete the `CATEGORY` of a person at the specified `INDEX`.
<br>
The sequence diagram below illustrates the interaction within the `Logic` component, taking `execute("deleteCategory 1 c/Clan")` API call as an example.

<puml src="diagrams/DeleteCategorySequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `deleteCategory 1 c/Clan` Command" />
<br>

<box type="info" seamless>

**Note:** The lifeline for `DeleteCategoryCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

#### How the feature is implemented

The feature of deleting category is implemented by the `DeleteCategoryCommand` and `DeleteCategoryCommandParser` classes.
<br>
The `DeleteCategoryCommand` class handles the logic of deleting one of user's category.
<br>
The execute class in `DeleteCategoryCommand` class checks if the input category is existing.
<br>
The `DeleteCategoryCommandParser` class parses user input into an `DeleteCategoryCommand` object. It validates the input and extracts the necessary information to instantiate an `DeleteCategoryCommand`.
<br>
If the format of command is correct, the `DeleteCategoryCommand` class will try to call the `deleteEntry` method from `Parser` class to delete the corresponding category.
<br>
If the format of command is wrong or the category does not exist, the `DeleteCategoryCommand` or `DeleteCategoryCommandParser` class will throw an exception.
<br>

#### Why it is implemented that way

Implementing the deleteCategory function this way ensures that users have the ability to maintain accurate and relevant data within their address book.
<br>
By using a dedicated command parser, the application robustly handles input validation and error handling, making the feature reliable and user-friendly.
<br>

#### Alternatives considered

One alternative considered was to limit the deletion to a single category per command to simplify the user interface and reduce the risk of accidental deletions.
<br>
However, to enhance usability and efficiency for users who manage extensive profiles, we decided to enable batch deletion of categories.
<br>
This approach allows users to remove multiple categories in a single command, making the management of contact data more efficient, especially when multiple outdated or erroneous categories need to be corrected simultaneously.
<br>
While this increases the complexity of the command, it significantly improves productivity for power users who need to perform bulk updates.
<br>
This decision was balanced with robust error handling and confirmations to mitigate the risk of accidental deletions.
<br>

<box type="info" seamless>

**Note:** There are some caveat for `deleteCategory` command:
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​
* The category **must exist**.
* The name of person can not be deleted.

</box>


### Edit function

The new edit function allows user to be able to edit any category based they want.<br>
<br>
The sequence diagram below illustrates the interaction within the `Logic` component, taking `execute("edit 1 c/Clan d/rainbow")` API call as an example.

<puml src="diagrams/EditSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `edit 1 c/Clan d/rainbow` Command" />

<box type="info" seamless>

**Note:** The lifeline for `EditCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

#### 1. How the feature is implemented

The edit feature in the address book application is implemented through the `EditCommand` and `EditCommandParser` classes.
<br>
The `EditCommand` class handles the logic of editing a person's details, such as category, description, and tags.
<br>
It uses an inner class, `EditPersonDescriptor`, to hold the values to be edited, allowing partial updates to a person's information.
<br>
The execute method in `EditCommand` performs the actual update, ensuring that the edited person does not duplicate existing entries.
<br>
The `EditCommandParser` class parses user input into an `EditCommand` object. It validates the input and extracts the necessary information
(like index, category, description, and tags) to create an `EditPersonDescriptor`, which is then used to instantiate an `EditCommand`.
<br>
Below is the method used to instantiate the `EditPersonDescriptor` used to "remember" what category and description is being changed.
<br>
```
editPersonDescriptor.set(category, ParserUtil.parse(category, category));
editPersonDescriptor.setCategory(category);
```
Additionally, in the `EditCommand` class, the method:
```
Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);
```
Creates this `EditPersonDescriptor` for it to edit the necessary code implemented by the user.
<br>

#### 2. Why it is implemented that way

This implementation segregates the parsing of user input from the execution logic, adhering to the single responsibility principle
and making the code more maintainable and testable. The `EditCommand` class focuses solely on the business logic of editing a person's details,
while the `EditCommandParser` deals with interpreting the user's input. This separation allows for clearer structure and easier debugging,
as each class has a distinct responsibility. The use of `EditPersonDescriptor` as a way to encapsulate the editable attributes of a person enables a
flexible design where only specified fields can be updated, enhancing the user experience by allowing partial edits.
<br>

#### 3. Alternatives considered
Alternatives that might have been considered include implementing the parsing logic directly within the `EditCommand class`, which would reduce the number
of classes and potentially simplify the command's initiation process. However, this approach could lead to a more complex `EditCommand` class, making it
harder to manage and maintain. Another alternative could be the use of reflection to dynamically update fields in the `Person` class based on input,
which would make adding new fields easier. However, this could increase complexity and decrease code readability and security due to the dynamic nature of reflection.
Additionally, the `EditPersonDescriptor` could be completely remove as the implementation of the `Entry` class would allow the edit of categories directly.
<br>


### Find function

The Find Command feature within the provided address book application is structured to enable users to search for persons based on specified criteria.
<br>

#### Implementation

The feature is implemented through several interconnected components:

1. **FindCommand Class**: This is the core of the feature, encapsulating the logic to find and list all persons whose fields match the specified criteria. It leverages a `PersonFieldsContainKeywordPredicate` to filter matching persons. Upon execution, it updates the model's filtered person list to reflect the search results.

2. **PersonFieldsContainKeywordPredicate Class**: Represents the criteria used to filter persons. It checks if any of the person's fields match the specified keywords in their categories, descriptions, or tags. This flexibility allows for a more nuanced search capability.

3. **FindCommandParser Class**: Parses the user input into a usable format for the `FindCommand`. It ensures the input adheres to the expected format, extracting relevant information to create a `PersonFieldsContainKeywordPredicate`.

4. **Model Interface**: Defines the API that the `FindCommand` interacts with, specifically the `updateFilteredPersonList` method that updates the list of displayed persons based on the predicate.
<br>

#### Rationale

The design choices made in implementing the Find Command are based on several considerations:

- **Modularity and Single Responsibility**: Each class has a clear and distinct role. `FindCommand` handles command execution, `PersonFieldsContainKeywordPredicate` defines the search criteria, and `FindCommandParser` processes user input. This separation of concerns makes the code more maintainable and extendable.

- **Flexibility in Search Criteria**: By using a predicate that can filter based on categories, descriptions, and tags, the implementation provides a versatile search functionality. This approach caters to various user needs, allowing for more detailed queries.

- **User-Friendly Command Syntax**: The command syntax is designed to be intuitive, with clear prefixes indicating the type of criteria (category, description, tag). This choice aims to enhance usability, making it easier for users to construct their search queries.

- **Enables Batch Processing**: Specifying multiple `c/<category> d/<description>` allows the user to look for multiple people who satisfy the at least one of the `c/ d/`.

- **Tags**: Finding with tags also allows batch processing, which enhances the users' experience in searching for people who belong to different tags.
<br>

#### Alternatives Considered

1. **Regular Expression Searches**: Implementing search functionality that allows for regular expression patterns could provide even more flexibility. However, this might increase the complexity of the user interface and require users to have knowledge of regular expressions.

2. **Separate Commands for Different Criteria**: Creating separate commands for searching by category, description, and tag could simplify the implementation. However, this approach was likely rejected because it would complicate the user experience, requiring users to remember and choose from multiple commands based on their search needs.

In conclusion, the Find Command is implemented with a focus on flexibility, usability, and maintainability, balancing advanced search capabilities with ease of use for the end user. The chosen implementation provides a solid foundation that can be easily extended or modified as the application evolves.
<br>


### Group function

The group command function groups `persons` based on the `category` the user specifies.
<br>
The sequence diagram below illustrates the interaction within `Logic` component, taking `execute("group c/Clan)` API call as an example.

<puml src="diagrams/GroupSequenceDiagram.puml" alt="Group Sequence Diagram" />

<box type="info" seamless>

**Note:** The lifeline for `GroupCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

#### 1. How the feature is implemented

The group feature in the address book application is implemented through the `GroupCommand`, `GroupCommandParser`
, `ModelManager` and `Group` classes. The `GroupCommandParser` parses user input, validates the input and extracts `categories` and
parse it into the `GroupCommand` object. The `GroupCommand` object will then instantiate the `ModelManger` to create a `Group` object by
provide an `ArrayList` of persons extracted from the addressbook. The `Group` object would then call the method `Group`, which will group
this `ArrayList` of persons into another `ArrayList` of groups. `ModelManager` will then create a new address book called `GroupAddressBook`
and store this `ArrayList` of groups. This will then later be called in the `UI`.
<br>

#### 2. Why is it implemented that way
The feature is implemented this way to ensure that `group` command does not group the original person list, leaving the person list sorted
by the latest entry. This also means that the user will have to call `group` everytime the user wants to group the person list according to
their customisation. Additionally, the `GroupAddressBook` is essentially a list of `persons` with their `Name` as the Group name/ specified
category and their categories as the list of people in the same category.
<br>

#### 3. Alternatives considered
Initially, we wanted to just use the original address book to sort the person list, then subsequently group them within the same address book.
However, this would mean that there will be less flexibility for the user. Also, it imposes a lot of difficulty in terms of displaying it in the
UI and constantly deleting and creating groups.
<br>


### Redo function

The redo command provides users the ability to revert the address book back to the state following an undo operation.
<br>
This function is particularly useful in a user environment where changes need to be reviewed and possibly reverted temporarily before final confirmation.
<br>
The sequence diagrams below illustrates the interaction within the `Logic` and `Model` components, taking `execute("redo")` API call as an example.

<puml src="diagrams/RedoSequenceDiagram-Logic.puml" alt="Interactions Inside the Logic Component for the `redo` Command" />

<puml src="diagrams/RedoSequenceDiagram-Model.puml" alt="Interactions Inside the Model Component for the `redo` Command" />

<box type="info" seamless>

**Note:** The lifeline for `RedoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

#### How the feature is implemented

The feature of redo is implemented by the `RedoCommand`, `UndoHistory` classes and some functions from `ModelManager` class.
<br>
The `RedoCommand` class handles the logic of recovering the state of address book back to the state before undo command.
<br>
The execute class in `RedoCommand` class checks if the current state of address book allows redo.
<br>
The `UndoHistory` represents a history of commands that have been undone in the address book, allowing for redo operations.
<br>
In the `ModelManager` class, `undoHistory` field stores the history of **non-group** operations, `groupUndoHistory` field stores the history of **group** operations.
<br>
The stack `undoActionTracker` tracks actions that have been undone. It is referenced during redo operations to determine which type of action (group or non-group) should be redone.
<br>
If the user has ever performed an **undo** operation, the `ModelManager` class call the `redo` method to push the current address book state to the history of command and load the undone state from the history of undo. 
<br>
If the history of **undo** is empty, `RedoCommand` will throw an exception.
<br>

#### Why it is implemented that way

Implementing the redo feature this way provides a straightforward and reliable method to manage changes within the application.
<br>
It allows users to experiment with different modifications without the risk of permanently altering data.
<br>

#### Alternatives Considered

Since the non-group operation and group operation have different logic, we have to distinguish between them.
<br>
If a command performs both group and non-group operations, the undo and redo commands need to be redesigned.
<br>
Fortunately, only the **clear** command has this feature, and this command does not allow undo and redo.

<box type="info" seamless>

**Note:** The **clear** command will remove all undo and redo history. This command cannot be undone or redone.

</box>

### Undo function

The undo command provides users the ability to revert the address book back to the state before a command was executed.
<br>
The previous command has to be one that changes the internal state of the address book (except for clear command):
* Add or delete a person.
* Add or delete or edit the category of a person.
* Group the people based on their categories.

This means that commands that do not change the internal state of the address book cannot be undone, for example:
* Find
* List
* Help
* Exit

The sequence diagrams below illustrates the interaction within the `Logic` and `Model` components, taking `execute("undo")` API call as an example.

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="Interactions Inside the Logic Component for the `redo` Command" />

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="Interactions Inside the Model Component for the `redo` Command" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.

</box>

#### How the feature is implemented

The feature of undo is implemented by the `UndoCommand`, `CommandHistory` classes and some methods from `ModelManager` class. <br>
* The `CommandHistory` class makes use of a deque to record the states of the address book after each undoable command. <br>
* For `AddCommand` and `DeleteCommand`, the state of the address book will be pushed into the `CommandHistory` deque before executing the commands. <br>
* For `AddCategoryCommand`, `DeleteCategory` and `EditCommand`, a state of the address book with the original person will be pushed into the `CommandHistory` deque before executing the commands. <br>
* For `GroupCommand`, the group address book state will be pushed into a `CommandHistory` instance for group address book before the command is executed. <br>
* To distinguish group and non-group commands so that the correct address book state is reset, a stack called `ActionTracker` will be used to record the type of command:
  * Boolean value `true` for the command executed is a group command.
  * Boolean value `false` for non-group command executed.
* The above logic is handled in ModelManager class.
* After which, `UndoCommand` class will execute the command by calling the `undo` method in `ModelManager` class, which will pop the `CommandHistory` deque, to retrieve the previous state of the address book.


#### Why it is implemented that way

* Storing the address book states for both group and non-group commands allows the differentiation of the type of commands, such that group and non-group commands can be undone smoothly without confusion.
* Using a deque to store the states of address book as it satisfies the criterion of undoing the most recent command by retrieving the most recent address book state.
* The `ActionTracker` stack also allows the clear distinction between group and non-group commands.
<br>

#### Alternatives Considered

* Saving the entire state of an address book when a command is executed might be costly if the address book size is huge.
* A less costly way of undoing could be implemented in the future to make the app more robust.

<box type="info" seamless>

**Note:** The **clear** command will remove all undo and redo history. This command cannot be undone or redone.

</box>

--------------------------------------------------------------------------------------------------------------------
## **Planned Enhancement**
1. Improve the UI so that the users can customize the window size.
2. Make the feedback box scroll bar visible.
3. Enable viewing a person's full detail when selecting a person in the person list panel.
4. Edit function will allow the user to edit the category.
5. Implement ungroup command.
6. Implement SaveGroup command.




--------------------------------------------------------------------------------------------------------------------
## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* for clan managers or hard core gamers of the game
* has a need to manage a significant number of contacts
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage contacts faster than a typical mouse/GUI driven app


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​          | I want to …​                                                                              | So that I can…​                                                             |
|----------|---------------------|----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `* * `   | Rainbow Dragon user | create and manage multiple character profiles, including their names, classes, and progress  | keep track of my gaming experience for different games                         |
| `* * `   | Rainbow Dragon user | add custom notes or tags to character profiles                                               | jot down important details or strategies for future reference                  |
| `* * `   | Rainbow Dragon user | learn comprehensive help documentation and tutorials accessible within the interface         | learn its features and commands more efficiently                               |
| `* * `   | Rainbow Dragon user | easily search for specific characters or games within the CLI tool                           | quickly access the information I need without navigating through complex menus |
| `* * `   | Rainbow Dragon user | gain insights and analytics about my gaming habits and progress from the CLI tool            | identify areas for improvement and set goals for future gameplay               |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `RainbowDragon` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete a person**

**MSS**

1.  User requests to list persons
2.  RainbowDragon shows a list of persons
3.  User requests to delete a specific person in the list
4.  RainbowDragon deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. RainbowDragon shows an error message.

      Use case resumes at step 2.

**Use case: Add a person**

**MSS**

1.  User requests to add a person
2.  RainbowDragon prompts user to enter person's details
3.  User enters person's details
4.  RainbowDragon validates the details
5.  RainbowDragon adds the person to the list.

    Use case ends.

**Extensions**

* 4a. User cancels the operation.

  Use case ends.

* 4b. User enters invalid details.

    * 4b1. RainbowDragon shows an error message.

      Use case resumes at step 2.

**Use case: Edit a person's details**

**MSS**

1.  User requests to edit a person's details
2.  RainbowDragon prompts user to select a person from the list
3.  User selects a person from the list
4.  RainbowDragon shows the person's details.
5.  User edits the details
6.  RainbowDragon validates the edited details
7.  RainbowDragon updates the person's details

    Use case ends.

**Extensions**

* 3a. The list is empty.

  Use case ends.

* 5a. User cancels the operation.

  Use case ends.

* 6a. User enters invalid details.

    * 6a1. RainbowDragon shows an error message.

      Use case resumes at step 5.

**Use case: View all persons**

**MSS**

1.  User requests to view all persons
2.  RainbowDragon shows a list of all persons.

    Use case ends.

**Use case: Search for a person**

**MSS**

1.  User requests to search for a person
2.  RainbowDragon prompts user to enter search keyword
3.  User enters search keyword
4.  RainbowDragon searches for persons matching the keyword
5.  RainbowDragon shows a list of matching persons

    Use case ends.

**Extensions**

* 3a. User cancels the operation.

  Use case ends.

**Use case: View all persons**

**MSS**

1.  User requests to view all persons
2.  RainbowDragon shows a list of all persons

    Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_.
2.  Java `11` or above installed in the user's device.
3.  The application should not have access to the internet.
4.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5.  The user should be someone who plays strategy games, MOBAs (Multiplayer Online Battle Arenas), or certain RPGs (Role-Playing Games).
6.  The application will provide comprehensive documentation and tutorials for users to get the most out of the application.


*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **AddressBook**: A software application used to store and manage contact information, such as names, phone numbers, and email addresses.
* **Actor**: In the context of use cases, an actor is a user or system component that interacts with the system to achieve a specific goal.
* **Use Case**: A description of how a system interacts with its users or other systems to achieve a particular goal.
* **MSS**: Stands for Main Success Scenario, which outlines the basic steps that should occur when a use case is executed successfully.
* **Extension**: Describes alternative paths or exceptional conditions that may occur during the execution of a use case.
* **GUI**: Stands for Graphical User Interface, which is a visual way for users to interact with a software application using graphical elements such as windows, buttons, and menus.
* **CLI**: Stands for Command Line Interface, which is a text-based way for users to interact with a software application by typing commands into a terminal or command prompt.
* **Timestamp**: A record of the date and time when an event occurred, often used for tracking changes or actions in a system.
* **Status Bar**: A horizontal bar typically located at the bottom of a window or application interface, providing information about the current status or state of the system.
* **Exploratory Testing**: A testing approach where testers explore the software application freely, without predefined test cases, to discover bugs or issues.
* **Jar file**: Stands for Java Archive file, which is a compressed file format used to package Java applications, libraries, or components.
* **Prerequisites**: Conditions or actions that must be met or completed before a test case can be executed.
* **Test Case**: A detailed description of steps to be followed and expected outcomes to be observed when testing a specific aspect or feature of a software application.
* **Corrupted Data File**: A file that has been damaged or altered in such a way that it cannot be read or processed correctly by the software application.
* **Status Message**: Information displayed to the user indicating the outcome or result of an action performed by the system.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
