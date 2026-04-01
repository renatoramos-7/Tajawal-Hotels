# Architecture Diagrams

This folder keeps the repository architecture documentation close to the code and easy to evolve in pull requests.

## Why Mermaid

- GitHub renders Mermaid blocks natively in Markdown
- diagrams stay versioned as text
- updates are simple when packages, flows, or tests change

## Recommended Structure

- `docs/architecture/README.md`: central index for high-level diagrams
- optional future files in the same folder for sequence diagrams or feature-specific flows

## Layered View

```mermaid
flowchart TD
    subgraph Presentation["Presentation layer"]
        Activities["Activities<br/>HotelListActivity<br/>DetailsActivity<br/>ImageViewerActivity"]
        Contracts["Contracts<br/>View / Presenter contracts"]
        Presenters["Presenters<br/>HotelListPresenter<br/>DetailsPresenter<br/>ImageViewerPresenter"]
    end

    subgraph Data["Data layer"]
        Repository["HotelsRepository"]
        Remote["Remote data source<br/>NetworkService (Retrofit)"]
        Local["Local data source<br/>HotelProvider (PaperDB)"]
        Models["Models<br/>HotelModel / HotelsModel / SummaryModel"]
    end

    subgraph Infrastructure["Infrastructure"]
        DI["Dagger 2<br/>AppComponent + Modules"]
        Rx["RxJava schedulers"]
        Libs["Glide / Google Maps / Support libs"]
    end

    Activities --> Contracts
    Contracts --> Presenters
    Presenters --> Repository
    Repository --> Remote
    Repository --> Local
    Remote --> Models
    Local --> Models
    DI --> Activities
    DI --> Presenters
    DI --> Repository
    Repository --> Rx
    Activities --> Libs
```

## Main MVP Data Flow

```mermaid
sequenceDiagram
    actor User
    participant View as HotelListActivity (View)
    participant Presenter as HotelListPresenter
    participant Repository as HotelsRepository
    participant Remote as NetworkService
    participant Local as HotelProvider

    User->>View: Open hotel list
    View->>Presenter: onStart() / getHotelList()
    Presenter->>View: showProgressBar()
    Presenter->>Repository: getHotelList()
    Repository->>Remote: getHotels()
    Remote-->>Repository: HotelsModel
    Repository->>Local: add(hotels)
    Local-->>Repository: cached hotel list
    Repository-->>Presenter: first non-empty list
    Presenter->>View: createAdapter(list)
    Presenter->>View: showAdapter()
    Presenter->>View: hideProgressBar()

    User->>View: Tap hotel item
    View->>Presenter: onItemClick(position)
    Presenter->>View: openDetails(hotelId)

    User->>View: Open details screen
    View->>Presenter: getHotelById()
    Presenter->>Repository: getHotelById(hotelId)
    Repository->>Local: getById(hotelId)
    Local-->>Presenter: HotelModel
    Presenter->>View: render details and image click
```

## Test Coverage View

```mermaid
flowchart LR
    subgraph Tests["Unit tests"]
        ListTest["HotelListPresenterTest"]
        DetailsTest["DetailsPresenterTest"]
        ImageTest["ImageViewerPresenterTest"]
    end

    subgraph Presenters["Presentation logic under test"]
        ListPresenter["HotelListPresenter"]
        DetailsPresenter["DetailsPresenter"]
        ImagePresenter["ImageViewerPresenter"]
    end

    subgraph Collaborators["Mocked collaborators"]
        ViewMocks["Contract Views"]
        RepositoryMock["HotelsRepository"]
    end

    ListTest --> ListPresenter
    DetailsTest --> DetailsPresenter
    ImageTest --> ImagePresenter
    ListPresenter --> ViewMocks
    ListPresenter --> RepositoryMock
    DetailsPresenter --> ViewMocks
    DetailsPresenter --> RepositoryMock
    ImagePresenter --> ViewMocks
```

## Notes For Future Updates

- keep package names aligned with the diagrams when screens or modules change
- update the data flow whenever the repository strategy changes
- extend the testing diagram if repository or instrumentation coverage grows
