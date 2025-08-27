package com.rinne.shared.network.model.deprecated

import com.rinne.shared.network.model.deprecated.books.BookDetails
import com.rinne.shared.network.model.deprecated.books.BooksHomeInfo
import com.rinne.shared.network.model.deprecated.books.asInfo
import com.rinne.shared.network.model.deprecated.courses.CourseAccessStatus
import com.rinne.shared.network.model.deprecated.courses.CourseCompletionStatus
import com.rinne.shared.network.model.deprecated.courses.CourseContent
import com.rinne.shared.network.model.deprecated.courses.CourseDetails
import com.rinne.shared.network.model.deprecated.courses.CourseSection
import com.rinne.shared.network.model.deprecated.courses.CourseSectionInfo
import com.rinne.shared.network.model.deprecated.courses.CourseThemeCompletionStatus
import com.rinne.shared.network.model.deprecated.courses.CourseThemeInfo
import com.rinne.shared.network.model.deprecated.courses.asInfo
import com.rinne.shared.network.model.deprecated.memorizer.MemorizerCardInfo
import com.rinne.shared.network.model.deprecated.memorizer.MemorizerDetails
import com.rinne.shared.network.model.deprecated.memorizer.MemorizerFolderInfo
import com.rinne.shared.network.model.deprecated.memorizer.MemorizerFolderType
import com.rinne.shared.network.model.deprecated.memorizer.MemorizerSetDetails
import com.rinne.shared.network.model.deprecated.memorizer.asInfo
import com.rinne.shared.network.model.deprecated.scopes.ScopeDetails
import com.rinne.shared.network.model.deprecated.scopes.ScopesInfo
import com.rinne.shared.network.model.deprecated.tasks.TaskInfo
import com.rinne.shared.network.model.deprecated.tasks.TasksFolderInfo
import com.rinne.shared.network.model.deprecated.tasks.TasksFolderType
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.ExperimentalTime

object Mock {

    @OptIn(ExperimentalTime::class)
    fun draftsInfo() = DraftsInfo(
        username = "tomrinne",
        drafts = List(5) {
            DraftInfo(
                id = "$it",
                title = "My new draft ${it + 1}",
                text = "Text",
                dateTime = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
            )
        },
    )

    fun homeInfo(): HomeInfo {
        val courses = courses().first()

        return HomeInfo(
            subscriptions = listOf(
                HomeSubscription.Course(
                    info = courses.asInfo(),
                    activeTheme = courses.activeTheme.info,
                ),
                HomeSubscription.Page(
                    info = pageDetails()[0].asShortInfo(),
                    articles = articleInfos().items.take(3)
                ),
                HomeSubscription.Page(
                    info = pageDetails()[1].asShortInfo(),
                    articles = articleInfos().items.take(3)
                ),
                HomeSubscription.Page(
                    info = pageDetails()[2].asShortInfo(),
                    articles = articleInfos().items.take(3)
                ),
                HomeSubscription.Community(
                    info = communities()[1].asInfo(),
                    articles = emptyList(),
                ),
            )
        )
    }

    fun profileInfo() = ProfileInfo(
        id = "1",
        name = "Tom Rinne",
        username = "tomsrinneganom",
        avatar = "https://pbs.twimg.com/profile_images/1190668443802099713/z0_SgC_b_400x400.jpg",
    )

    fun pageDetails() = listOf(
        PageDetails(
            id = "1",
            tag = "Ukraine/City",
            type = PageType.PLACE,
            cover = "https://ukraine.ua/wp-content/uploads/2020/11/Beautiful-panoramic-view-of-the-Odessa-State-Academic-Opera-and-Ballet-Theater-early-in-the-morning-without-people.LALS-STOCK.shutterstock-1536x864.jpg",
            title = "Odessa",
            text = "Odessa (also spelled Odesa) is the third most populous city and municipality in Ukraine and a major seaport and transport hub located in the south-west of the country, on the northwestern shore of the Black Sea. The city is also the administrative centre of the Odesa Raion and Odesa Oblast, as well as a multiethnic cultural centre. As of January 2021, Odesa's population was approximately 1,010,537. On 25 January 2023, its historic city centre was declared a World Heritage Site and added to the List of World Heritage in Danger by the UNESCO World Heritage Committee in recognition of its multiculturality and 19th-century urban planning. The declaration was made in response to the bombing of Odesa during the Russian invasion of Ukraine, which has damaged or destroyed buildings across the city.\n",
            relationshipTypes = emptyList(),
            filters = listOf(
                PageFilters.Location(RinneLatLng(46.478252, 30.738003))
            ),
            sectionsShortInfo = listOf(
                PageSectionShortInfo(
                    id = "3",
                    title = "Country",
                    text = "Ukraine",
                ),
                PageSectionShortInfo(
                    id = "1",
                    title = "Population",
                    text = "1 010 537",
                ),
                PageSectionShortInfo(
                    id = "2",
                    title = "Population rank",
                    text = "#3",
                ),
                PageSectionShortInfo(
                    id = "4",
                    title = "Weather",
                    text = "17°",
                ),
            ),
        ),
        PageDetails(
            id = "2",
            tag = "Science",
            type = PageType.DEFAULT,
            cover = "https://s3.eu-central-1.amazonaws.com/shenkar-production/header_images/h_image_school_athenes.jpg?1409726037",
            title = "Philosophy",
            text = "Philosophy ('love of wisdom' in Ancient Greek) is a systematic study of general and fundamental questions concerning topics like existence, reason, knowledge, value, mind, and language. It is a rational and critical inquiry that reflects on its methods and assumptions.",
            relationshipTypes = emptyList(),
            filters = emptyList(),
            sectionsShortInfo = emptyList(),
        ),
        PageDetails(
            id = "de",
            tag = "Language",
            type = PageType.DEFAULT,
            cover = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/D-A-CH_Flag.svg/900px-D-A-CH_Flag.svg.png",
            title = "German",
            text = "German is a West Germanic language in the Indo-European language family, mainly spoken in Western and Central Europe. It is the most spoken native language within the European Union. It is also the official (or co-official) language in Germany, Austria, Switzerland, Liechtenstein, and the Italian autonomous province of South Tyrol. It is also an official language of Luxembourg, Belgium and the Italian autonomous region of Friuli-Venezia Giulia, as well as a recognized national language in Namibia. There are also notable German-speaking communities in France (Alsace), the Czech Republic (North Bohemia), Poland (Upper Silesia), Slovakia (Košice Region, Spiš, and Hauerland), Denmark (North Schleswig), Romania and Hungary (Sopron). Overseas, sizeable communities of German-speakers are found in Brazil (Blumenau and Pomerode), South Africa (Kroondal), Namibia, among others, some communities have decidedly Austrian German or Swiss German characters (e.g. Pozuzo, Peru).",
            relationshipTypes = emptyList(),
            filters = emptyList(),
            sectionsShortInfo = emptyList(),
        ),
    )

    fun timelineDetails(pageId: String) = TimelineDetails(
        pageInfo = (pageDetails().firstOrNull { it.id == pageId }
            ?: pageDetails().random()).asShortInfo(),
        years = List(5) {
            TimelineYear(
                info = TimelineYearInfo("$it", "${1794 + it}"),
                events = List(3) {
                    TimelineYearEvent(
                        id = "$it",
                        cover = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Catherine_II_by_F.Rokotov_after_Roslin_%28c.1770%2C_Hermitage%29.jpg/960px-Catherine_II_by_F.Rokotov_after_Roslin_%28c.1770%2C_Hermitage%29.jpg",
                        title = "Odessa founded by decree of Catherine II of Russia.",
                        description = "The Flemish engineer working for the Russian Empress Catherine the Great, José de Ribas's collaborator Franz de Voland recommended the area of Khadjibey fortress as the site for the region's basic port: it had an ice-free harbor, breakwaters could be cheaply constructed that would render the harbor safe and it would have the capacity to accommodate large fleets. The Namestnik of Yekaterinoslav and Voznesensk, Platon Zubov (one of Catherine's favorites), supported this proposal. In 1794 Catherine issued a Rescript to José de Ribas: \"Considering favorable Khadjibey location... I order to establish here a navy harbor and trading pierce...\" and invested the first money (26.000 rubles) in construction. Franz de Voland drew up a plan that would end up being the city's plan.",
                        date = LocalDate(1794, 9, 2),
                    )
                }
            )
        }
    )


    fun memorizerDetails() = MemorizerDetails(
        userInfo = profileInfo(),
        folders = memorizerFolders(),
    )

    fun memorizerFolders() = listOf(
        MemorizerFolderInfo(
            id = "1",
            type = MemorizerFolderType.Default("Languages"),
            sets = memorizerSetOfCardsDetails("/languages").map { it.asInfo() },
        ),
        MemorizerFolderInfo(
            id = "2",
            type = MemorizerFolderType.Unsorted,
            sets = memorizerSetOfCardsDetails("/unsorted").map { it.asInfo() },
        ),
    )

    fun memorizerSetOfCardsDetails(
        path: String = "",
    ) = listOf(
        MemorizerSetDetails(
            id = "1",
            name = "Deutsch",
            path = path,
            cards = listOf(
                MemorizerCardInfo(
                    id = "1",
                    front = "Where is the dog?",
                    back = "Wo ist der Hund?",
                ),
                MemorizerCardInfo(
                    id = "2",
                    front = "What did you do today?",
                    back = "Was hast du heute gemacht?",
                ),
                MemorizerCardInfo(
                    id = "3",
                    front = "I would like to meet with you",
                    back = "Ich würde mich gerne mit dir treffen",
                )
            ),
            user = profileInfo(),
        )
    )


    fun articleDetails() = ArticleDetails(
        id = "1",
        rating = "+200",
        title = "How we can easy improve our city garden",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n",
        tags = listOf("history", "tag", "label"),
        mainTag = "Ukraine/Odessa",
        commentsCount = "200",
        bookmarked = Random.nextBoolean(),
        author = profileInfo(),
        dateTime = LocalDateTime(2025, 3, 1, 12, 0),
        discussion = Discussion(
            id = "1",
            comments = List(15) {
                DiscussionComment(
                    id = "$it",
                    authorInfo = profileInfo(),
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    rating = "200",
                    dateTime = LocalDateTime(2025, 4, 13, 12, 32),
                    answers = emptyList(),
                )
            }
        ),
        image = null,
    )

    fun articleInfos() = ArticlesInfo(
        tag = "Ukraine/Odessa",
        items = List(10) {
            ArticleInfo(
                id = "$it",
                title = "How we can easy improve our city garden",
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                tags = listOf("history", "tag", "label"),
                rating = "+200",
                commentsCount = "200",
                author = profileInfo(),
                dateTime = LocalDateTime(2025, 3, 1, 12, 0),
                image = null,
            )
        },
        filters = listOf("history", "tag", "label"),
    )

    fun collectionInfo(id: String = "1") = CollectionInfo(
        id = id,
        title = "Files",
        tag = "Odessa/WW2",
        items = List(6) {
            CollectionItem(
                id = "$it",
                cover = "https://www.ags-recordsmanagement.com/wp-content/uploads/sites/5/2023/04/history-of-public-archives.jpg",
                title = "File $it",
                subtitle = "PDF 2.4 MB"
            )
        },
        filters = listOf(
            CollectionFilter(
                id = "1",
                title = "#Label",
                applied = true,
            ),
            CollectionFilter(
                id = "2",
                title = "#Label",
                applied = false,
            ),
            CollectionFilter(
                id = "3",
                title = "#Label",
                applied = false,
            )
        )
    )

    fun communities() = listOf(
        CommunityDetails(
            id = "1",
            name = "Rinne",
            displayId = "rinne",
            image = "https://pbs.twimg.com/profile_images/1913343450226778112/HvdAq3mp_400x400.png",
            rating = "200",
            tags = listOf(CommunityTagInfo("rinne", "rinne"), CommunityTagInfo("apps", "apps")),
            articles = articleInfos().items,
            extensions = emptyList(),
        ),
        CommunityDetails(
            id = "2",
            name = "Bulgaria News",
            displayId = "bgNews",
            image = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Flag_of_Bulgaria.svg/1280px-Flag_of_Bulgaria.svg.png",
            rating = "200",
            tags = listOf(
                CommunityTagInfo("news", "news"),
                CommunityTagInfo("bulgaria", "bulgaria")
            ),
            articles = emptyList(),
            extensions = listOf(CommunityExtension.RssChannel("https://www.standartnews.com/rss?p=1"))
        ),
    )

    fun topics() = TopicsInfo(
        sections = listOf(
            TopicSection.Communities(communities().map { it.asInfo() })
        )
    )

    fun books() = listOf(
        BookDetails(
            id = "1",
            title = "Die Lage der arbeitenden Klasse in England",
            author = "Friedrich Engels",
            cover = "https://m.media-amazon.com/images/I/71x67f5PlrL._AC_UF894,1000_QL80_.jpg",
            text = "Arbeiter!\n" +
                    "\n" +
                    "  Euch widme ich ein Werk, in dem ich den Versuch gemacht habe, meinen deutschen Landsleuten ein treues Bild eurer Lebensbedingungen, eurer Leiden und Kämpfe, eurer Hoffnungen und Perspektiven zu zeichnen. Ich habe lange genug unter euch gelebt, um einiges von euren Lebensumständen zu wissen; ich habe ihrer Kenntnis meine ernsteste Aufmerksamkeit gewidmet; ich habe die verschiedenen offiziellen und nichtoffiziellen Dokumente studiert, soweit ich die Möglichkeit hatte, sie mir zu beschaffen – ich habe mich damit nicht begnügt, mir war es um mehr zu tun als um die nur \n" +
                    "  abstrakte Kenntnis meines Gegenstandes, ich wollte euch in euren Behausungen sehen, euch in eurem täglichen Leben beobachten, mit euch plaudern über eure Lebensbedingungen und Schmerzen, Zeuge sein eurer Kämpfe gegen die soziale und politische Macht eurer Unterdrücker. Ich verfuhr dabei so: Ich verzichtete auf die Gesellschaft und die Bankette, den Portwein und den Champagner der Mittelklasse und widmete meine Freistunden fast ausschließlich dem Verkehr mit einfachen \n" +
                    "  Arbeitern; ich bin froh und stolz zugleich, so gehandelt zu haben. Froh, weil ich mir auf diese Weise manche frohe Stunde verschaffte, während ich gleichzeitig euer wirkliches Leben kennenlernte – manche Stunde, die sonst vertan worden wäre in konventionellem Geschwätz und langweiliger Etikette; stolz, weil mir",
        ),
        BookDetails(
            id = "2",
            title = "Wissenschaft der Logik",
            author = "Georg Wilhelm Friedrich Hegel",
            cover = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/Wissenschaft_der_Logik.jpg/500px-Wissenschaft_der_Logik.jpg",
            text = "Arbeiter!\n" +
                    "\n" +
                    "  Euch widme ich ein Werk, in dem ich den Versuch gemacht habe, meinen deutschen Landsleuten ein treues Bild eurer Lebensbedingungen, eurer Leiden und Kämpfe, eurer Hoffnungen und Perspektiven zu zeichnen. Ich habe lange genug unter euch gelebt, um einiges von euren Lebensumständen zu wissen; ich habe ihrer Kenntnis meine ernsteste Aufmerksamkeit gewidmet; ich habe die verschiedenen offiziellen und nichtoffiziellen Dokumente studiert, soweit ich die Möglichkeit hatte, sie mir zu beschaffen – ich habe mich damit nicht begnügt, mir war es um mehr zu tun als um die nur \n" +
                    "  abstrakte Kenntnis meines Gegenstandes, ich wollte euch in euren Behausungen sehen, euch in eurem täglichen Leben beobachten, mit euch plaudern über eure Lebensbedingungen und Schmerzen, Zeuge sein eurer Kämpfe gegen die soziale und politische Macht eurer Unterdrücker. Ich verfuhr dabei so: Ich verzichtete auf die Gesellschaft und die Bankette, den Portwein und den Champagner der Mittelklasse und widmete meine Freistunden fast ausschließlich dem Verkehr mit einfachen \n" +
                    "  Arbeitern; ich bin froh und stolz zugleich, so gehandelt zu haben. Froh, weil ich mir auf diese Weise manche frohe Stunde verschaffte, während ich gleichzeitig euer wirkliches Leben kennenlernte – manche Stunde, die sonst vertan worden wäre in konventionellem Geschwätz und langweiliger Etikette; stolz, weil mir",
        ),
    )

    fun booksHomeInfo() = BooksHomeInfo(
        books = books().map { it.asInfo() },
        userInfo = profileInfo()
    )

    fun scopes() = listOf(
        ScopeDetails(
            id = "1",
            name = "German",
            cover = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/D-A-CH_Flag.svg/900px-D-A-CH_Flag.svg.png",
            pages = pageDetails().filter { it.id == "de" }.map { it.asShortInfo() },
            memorizer = memorizerSetOfCardsDetails().filter { it.id == "1" }.map { it.asInfo() },
            owner = profileInfo(),
        )
    )

    fun bookmarks(): BookmarksInfo {
        val articles = articleInfos().items

        val folders = listOf(
            BookmarkFolder(
                id = "1",
                bookmarks = listOf(
                    BookmarkInfo(
                        id = "lignolia",
                        name = "Lignolia DE",
                        description = "Improve your German with Lingolia’s free content. Revise grammar topics, learn new vocabulary and develop your reading and listening comprehension skills—all for free!\n" +
                                "\n" +
                                "Our articles about grammar are packed with examples and translations to help you understand even the trickiest of topics. Each article comes with one free interactive exercise where you can practise the basics. Expand your German vocabulary via our curated lists and train your listening and reading skills with our collection of texts and audio tracks.",
                        type = BookmarkType.Link("https://deutsch.lingolia.com/en/"),
                    ),
                    BookmarkInfo(
                        id = "de",
                        name = "German",
                        description = "German is a West Germanic language in the Indo-European language family, mainly spoken in Western and Central Europe. It is the most spoken native language within the European Union. It is also the official (or co-official) language in Germany, Austria, Switzerland, Liechtenstein, and the Italian autonomous province of South Tyrol. It is also an official language of Luxembourg, Belgium and the Italian autonomous region of Friuli-Venezia Giulia, as well as a recognized national language in Namibia. There are also notable German-speaking communities in France (Alsace), the Czech Republic (North Bohemia), Poland (Upper Silesia), Slovakia (Košice Region, Spiš, and Hauerland), Denmark (North Schleswig), Romania and Hungary (Sopron). Overseas, sizeable communities of German-speakers are found in Brazil (Blumenau and Pomerode), South Africa (Kroondal), Namibia, among others, some communities have decidedly Austrian German or Swiss German characters (e.g. Pozuzo, Peru).",
                        type = BookmarkType.Page("de"),
                    )
                ),
                type = BookmarkFolderType.Default("Deutsch"),
            ),
            BookmarkFolder(
                id = "2",
                bookmarks = listOf(
                    BookmarkInfo(
                        id = "article",
                        name = articles.first().title,
                        description = articles.first().text,
                        type = BookmarkType.Article(articles.first().id),
                    )
                ),
                type = BookmarkFolderType.Unsorted,
            )
        )

        return BookmarksInfo(
            owner = profileInfo(),
            folders = folders,
        )
    }

    fun tasks(idPrefix: String = "") = List(5) {
        TaskInfo(
            id = "$idPrefix-$it",
            title = "Task $it",
            subtitle = "Subtitle",
            done = Random.nextBoolean(),
            priority = 0,
        )
    }

    fun tasksFolders() = List(5) {
        TasksFolderInfo(
            id = "$it",
            tasks = tasks("folder $it"),
            type = TasksFolderType.Default("Folder $it")
        )
    }

    fun courses() = listOf(
        CourseDetails(
            id = "e83d5784-1f63-4b9d-920c-fd4dd2c52fde",
            cover = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/D-A-CH_Flag.svg/450px-D-A-CH_Flag.svg.png?20110824021807",
            name = "German language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A0",
                        name = "Level A0",
                    ),
                    content = germanCourseA0Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A0 $index",
                            info = CourseThemeInfo(
                                id = "Level A0 $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1",
                        name = "Level A1",
                    ),
                    content = germanCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 1 $index",
                            info = CourseThemeInfo(
                                id = "Level A1 $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 2",
                        name = "Level A2",
                    ),
                    content = germanCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 2 $index",
                            info = CourseThemeInfo(
                                id = "Level A2 $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 2",
                        name = "Level B1",
                    ),
                    content = germanCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 2 $index",
                            info = CourseThemeInfo(
                                id = "Level B1 $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 2",
                        name = "Level B2",
                    ),
                    content = germanCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 2 $index",
                            info = CourseThemeInfo(
                                id = "Level B2 $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 2",
                        name = "Level C1",
                    ),
                    content = germanCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 2 $index",
                            info = CourseThemeInfo(
                                id = "Level C1 $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 2",
                        name = "Level C2",
                    ),
                    content = germanCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 $index",
                            info = CourseThemeInfo(
                                id = "Level C2 $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "de1d6540-e9e9-4222-9616-e67a9cd7654c",
            cover = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Flag_of_Bulgaria.svg/2560px-Flag_of_Bulgaria.svg.png",
            name = "Bulgarian language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 bg",
                        name = "Level A1",
                    ),
                    content = bulgarianCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 bg $index",
                            info = CourseThemeInfo(
                                id = "Level A1 bg $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 bg",
                        name = "Level A2",
                    ),
                    content = bulgarianCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 bg $index",
                            info = CourseThemeInfo(
                                id = "Level A2 bg $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 bg",
                        name = "Level B1",
                    ),
                    content = bulgarianCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 bg $index",
                            info = CourseThemeInfo(
                                id = "Level B1 bg $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 bg",
                        name = "Level B2",
                    ),
                    content = bulgarianCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 bg $index",
                            info = CourseThemeInfo(
                                id = "Level B2 bg $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 bg",
                        name = "Level C1",
                    ),
                    content = bulgarianCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 bg $index",
                            info = CourseThemeInfo(
                                id = "Level C1  bg$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 bg",
                        name = "Level C2",
                    ),
                    content = bulgarianCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 bg $index",
                            info = CourseThemeInfo(
                                id = "Level C2 bg $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "1c4251be-6898-4427-b8bf-1d9ede5e26ff",
            cover = "https://upload.wikimedia.org/wikipedia/commons/0/03/Flag_of_Italy.svg",
            name = "Italian language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 it",
                        name = "Level A1",
                    ),
                    content = italianCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 it $index",
                            info = CourseThemeInfo(
                                id = "Level A1 it $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 it",
                        name = "Level A2",
                    ),
                    content = italianCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 it $index",
                            info = CourseThemeInfo(
                                id = "Level A2 it $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 it",
                        name = "Level B1",
                    ),
                    content = italianCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 it $index",
                            info = CourseThemeInfo(
                                id = "Level B1 it $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 it",
                        name = "Level B2",
                    ),
                    content = italianCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 it $index",
                            info = CourseThemeInfo(
                                id = "Level B2 it $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 it",
                        name = "Level C1",
                    ),
                    content = italianCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 it $index",
                            info = CourseThemeInfo(
                                id = "Level C1  it$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 it",
                        name = "Level C2",
                    ),
                    content = italianCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 it $index",
                            info = CourseThemeInfo(
                                id = "Level C2 it $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "9b783a04-2c8e-443d-98e4-1ede5e19d614",
            cover = "https://upload.wikimedia.org/wikipedia/en/9/9a/Flag_of_Spain.svg",
            name = "Spanish language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 es",
                        name = "Level A1",
                    ),
                    content = spanishCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 es $index",
                            info = CourseThemeInfo(
                                id = "Level A1 es $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 es",
                        name = "Level A2",
                    ),
                    content = spanishCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 es $index",
                            info = CourseThemeInfo(
                                id = "Level A2 es $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 es",
                        name = "Level B1",
                    ),
                    content = spanishCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 es $index",
                            info = CourseThemeInfo(
                                id = "Level B1 es $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 es",
                        name = "Level B2",
                    ),
                    content = spanishCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 es $index",
                            info = CourseThemeInfo(
                                id = "Level B2 es $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 es",
                        name = "Level C1",
                    ),
                    content = spanishCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 es $index",
                            info = CourseThemeInfo(
                                id = "Level C1  es$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 es",
                        name = "Level C2",
                    ),
                    content = spanishCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 es $index",
                            info = CourseThemeInfo(
                                id = "Level C2 es $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "4b0540f1-0e7c-41b4-8378-9dd25fde3feb",
            cover = "https://upload.wikimedia.org/wikipedia/en/c/c3/Flag_of_France.svg",
            name = "French language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 fr",
                        name = "Level A1",
                    ),
                    content = frenchCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 fr $index",
                            info = CourseThemeInfo(
                                id = "Level A1 fr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 fr",
                        name = "Level A2",
                    ),
                    content = frenchCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 fr $index",
                            info = CourseThemeInfo(
                                id = "Level A2 fr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 fr",
                        name = "Level B1",
                    ),
                    content = frenchCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 fr $index",
                            info = CourseThemeInfo(
                                id = "Level B1 fr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 fr",
                        name = "Level B2",
                    ),
                    content = frenchCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 fr $index",
                            info = CourseThemeInfo(
                                id = "Level B2 fr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 fr",
                        name = "Level C1",
                    ),
                    content = frenchCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 fr $index",
                            info = CourseThemeInfo(
                                id = "Level C1  fr$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 fr",
                        name = "Level C2",
                    ),
                    content = frenchCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 fr $index",
                            info = CourseThemeInfo(
                                id = "Level C2 fr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "152fb842-0ff0-4cf8-9835-a279bbf930e7",
            cover = "https://upload.wikimedia.org/wikipedia/commons/0/0b/English_language.svg",
            name = "English language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 en",
                        name = "Level A1",
                    ),
                    content = englishCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 en $index",
                            info = CourseThemeInfo(
                                id = "Level A1 en $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 en",
                        name = "Level A2",
                    ),
                    content = englishCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 en $index",
                            info = CourseThemeInfo(
                                id = "Level A2 en $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 en",
                        name = "Level B1",
                    ),
                    content = englishCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 en $index",
                            info = CourseThemeInfo(
                                id = "Level B1 en $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 en",
                        name = "Level B2",
                    ),
                    content = englishCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 en $index",
                            info = CourseThemeInfo(
                                id = "Level B2 en $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 en",
                        name = "Level C1",
                    ),
                    content = englishCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 en $index",
                            info = CourseThemeInfo(
                                id = "Level C1  en$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 en",
                        name = "Level C2",
                    ),
                    content = englishCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 en $index",
                            info = CourseThemeInfo(
                                id = "Level C2 en $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "7fabc817-1a82-4a66-9412-03d9f162e602",
            cover = "https://upload.wikimedia.org/wikipedia/commons/5/55/Ancient_Greek_Culture_Flag.jpg",
            name = "Ancient Greek language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 grc",
                        name = "Level A1",
                    ),
                    content = ancientGreekCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 grc $index",
                            info = CourseThemeInfo(
                                id = "Level A1 grc $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 grc",
                        name = "Level A2",
                    ),
                    content = ancientGreekCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 grc $index",
                            info = CourseThemeInfo(
                                id = "Level A2 grc $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 grc",
                        name = "Level B1",
                    ),
                    content = ancientGreekCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 grc $index",
                            info = CourseThemeInfo(
                                id = "Level B1 grc $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 grc",
                        name = "Level B2",
                    ),
                    content = ancientGreekCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 grc $index",
                            info = CourseThemeInfo(
                                id = "Level B2 grc $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 grc",
                        name = "Level C1",
                    ),
                    content = ancientGreekCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 grc $index",
                            info = CourseThemeInfo(
                                id = "Level C1  grc$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 grc",
                        name = "Level C2",
                    ),
                    content = ancientGreekCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 grc $index",
                            info = CourseThemeInfo(
                                id = "Level C2 grc $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "8af8ef91-9598-4bd0-ad7f-1089a61fa06d",
            cover = "https://upload.wikimedia.org/wikipedia/commons/f/fa/Flag_of_the_People%27s_Republic_of_China.svg",
            name = "Chinese language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 zh",
                        name = "Level A1",
                    ),
                    content = chineseCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 zh $index",
                            info = CourseThemeInfo(
                                id = "Level A1 zh $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 zh",
                        name = "Level A2",
                    ),
                    content = chineseCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 zh $index",
                            info = CourseThemeInfo(
                                id = "Level A2 zh $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 zh",
                        name = "Level B1",
                    ),
                    content = chineseCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 zh $index",
                            info = CourseThemeInfo(
                                id = "Level B1 zh $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 zh",
                        name = "Level B2",
                    ),
                    content = chineseCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 zh $index",
                            info = CourseThemeInfo(
                                id = "Level B2 zh $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 zh",
                        name = "Level C1",
                    ),
                    content = chineseCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 zh $index",
                            info = CourseThemeInfo(
                                id = "Level C1  zh$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 zh",
                        name = "Level C2",
                    ),
                    content = chineseCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 zh $index",
                            info = CourseThemeInfo(
                                id = "Level C2 zh $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "94556c24-7656-41e0-8a88-a2e7e5e226b5",
            cover = "https://upload.wikimedia.org/wikipedia/commons/5/51/Flag_of_North_Korea.svg",
            name = "Korean language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 ko",
                        name = "Level A1",
                    ),
                    content = koreanCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 ko $index",
                            info = CourseThemeInfo(
                                id = "Level A1 ko $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 ko",
                        name = "Level A2",
                    ),
                    content = koreanCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 ko $index",
                            info = CourseThemeInfo(
                                id = "Level A2 ko $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 ko",
                        name = "Level B1",
                    ),
                    content = koreanCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 ko $index",
                            info = CourseThemeInfo(
                                id = "Level B1 ko $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 ko",
                        name = "Level B2",
                    ),
                    content = koreanCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 ko $index",
                            info = CourseThemeInfo(
                                id = "Level B2 ko $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 ko",
                        name = "Level C1",
                    ),
                    content = koreanCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 ko $index",
                            info = CourseThemeInfo(
                                id = "Level C1  ko$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 ko",
                        name = "Level C2",
                    ),
                    content = koreanCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 ko $index",
                            info = CourseThemeInfo(
                                id = "Level C2 ko $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "334dba33-6b72-4d13-92ce-fdba69966708",
            cover = "https://upload.wikimedia.org/wikipedia/commons/b/b4/Flag_of_Turkey.svg",
            name = "Turkish language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 tr",
                        name = "Level A1",
                    ),
                    content = turkishCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 tr $index",
                            info = CourseThemeInfo(
                                id = "Level A1 tr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 tr",
                        name = "Level A2",
                    ),
                    content = turkishCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 tr $index",
                            info = CourseThemeInfo(
                                id = "Level A2 tr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 tr",
                        name = "Level B1",
                    ),
                    content = turkishCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 tr $index",
                            info = CourseThemeInfo(
                                id = "Level B1 tr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 tr",
                        name = "Level B2",
                    ),
                    content = turkishCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 tr $index",
                            info = CourseThemeInfo(
                                id = "Level B2 tr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 tr",
                        name = "Level C1",
                    ),
                    content = turkishCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 tr $index",
                            info = CourseThemeInfo(
                                id = "Level C1  tr$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 tr",
                        name = "Level C2",
                    ),
                    content = turkishCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 tr $index",
                            info = CourseThemeInfo(
                                id = "Level C2 tr $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "8c96ce2c-4793-4b9d-8cd8-d57ef201a836",
            cover = "https://upload.wikimedia.org/wikipedia/commons/0/0e/Flag_of_the_Arabic_language.svg",
            name = "Arabic language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 ar",
                        name = "Level A1",
                    ),
                    content = arabicCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 ar $index",
                            info = CourseThemeInfo(
                                id = "Level A1 ar $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 ar",
                        name = "Level A2",
                    ),
                    content = arabicCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 ar $index",
                            info = CourseThemeInfo(
                                id = "Level A2 ar $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 ar",
                        name = "Level B1",
                    ),
                    content = arabicCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 ar $index",
                            info = CourseThemeInfo(
                                id = "Level B1 ar $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 ar",
                        name = "Level B2",
                    ),
                    content = arabicCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 ar $index",
                            info = CourseThemeInfo(
                                id = "Level B2 ar $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 ar",
                        name = "Level C1",
                    ),
                    content = arabicCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 ar $index",
                            info = CourseThemeInfo(
                                id = "Level C1  ar$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 ar",
                        name = "Level C2",
                    ),
                    content = arabicCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 ar $index",
                            info = CourseThemeInfo(
                                id = "Level C2 ar $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "56423850-6b2a-48aa-bf4c-fcd994893b00",
            cover = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/20127d25-c622-47fe-9252-a6e48014395f/ddirscz-16c91fea-5bfb-4a7e-b822-bf153255e8e8.png/v1/fill/w_1248,h_640,q_70,strp/flag_of_latin_languages_by_politicalflags_ddirscz-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9NjU3IiwicGF0aCI6IlwvZlwvMjAxMjdkMjUtYzYyMi00N2ZlLTkyNTItYTZlNDgwMTQzOTVmXC9kZGlyc2N6LTE2YzkxZmVhLTViZmItNGE3ZS1iODIyLWJmMTUzMjU1ZThlOC5wbmciLCJ3aWR0aCI6Ijw9MTI4MCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.iGmtf3eCFx0weuX-s4GYZG5GmSmP7emQWpXD6nO385k",
            name = "Latin language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 la",
                        name = "Level A1",
                    ),
                    content = latinCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 la $index",
                            info = CourseThemeInfo(
                                id = "Level A1 la $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 la",
                        name = "Level A2",
                    ),
                    content = latinCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 la $index",
                            info = CourseThemeInfo(
                                id = "Level A2 la $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 la",
                        name = "Level B1",
                    ),
                    content = latinCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 la $index",
                            info = CourseThemeInfo(
                                id = "Level B1 la $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 la",
                        name = "Level B2",
                    ),
                    content = latinCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 la $index",
                            info = CourseThemeInfo(
                                id = "Level B2 la $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 la",
                        name = "Level C1",
                    ),
                    content = latinCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 la $index",
                            info = CourseThemeInfo(
                                id = "Level C1  la$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 la",
                        name = "Level C2",
                    ),
                    content = latinCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 la $index",
                            info = CourseThemeInfo(
                                id = "Level C2 la $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "2057bb1d-85db-4265-a4ed-3c3d388e9ede",
            cover = "https://upload.wikimedia.org/wikipedia/commons/f/f5/Flag_of_Esperanto.svg",
            name = "Esperanto language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 epo",
                        name = "Level A1",
                    ),
                    content = esperantoCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 epo $index",
                            info = CourseThemeInfo(
                                id = "Level A1 epo $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 epo",
                        name = "Level A2",
                    ),
                    content = esperantoCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 epo $index",
                            info = CourseThemeInfo(
                                id = "Level A2 epo $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 epo",
                        name = "Level B1",
                    ),
                    content = esperantoCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 epo $index",
                            info = CourseThemeInfo(
                                id = "Level B1 epo $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 epo",
                        name = "Level B2",
                    ),
                    content = esperantoCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 epo $index",
                            info = CourseThemeInfo(
                                id = "Level B2 epo $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 epo",
                        name = "Level C1",
                    ),
                    content = esperantoCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 epo $index",
                            info = CourseThemeInfo(
                                id = "Level C1  epo$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 epo",
                        name = "Level C2",
                    ),
                    content = esperantoCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 epo $index",
                            info = CourseThemeInfo(
                                id = "Level C2 epo $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "c2db9cc0-e57b-41dd-acad-5f7ef4a0d317",
            cover = "https://upload.wikimedia.org/wikipedia/commons/f/ff/Flag_of_Serbia.svg",
            name = "Serbian language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 srp",
                        name = "Level A1",
                    ),
                    content = serbianCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 srp $index",
                            info = CourseThemeInfo(
                                id = "Level A1 srp $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 srp",
                        name = "Level A2",
                    ),
                    content = serbianCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 srp $index",
                            info = CourseThemeInfo(
                                id = "Level A2 srp $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 srp",
                        name = "Level B1",
                    ),
                    content = serbianCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 srp $index",
                            info = CourseThemeInfo(
                                id = "Level B1 srp $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 srp",
                        name = "Level B2",
                    ),
                    content = serbianCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 srp $index",
                            info = CourseThemeInfo(
                                id = "Level B2 srp $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 srp",
                        name = "Level C1",
                    ),
                    content = serbianCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 srp $index",
                            info = CourseThemeInfo(
                                id = "Level C1  srp$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 srp",
                        name = "Level C2",
                    ),
                    content = serbianCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 srp $index",
                            info = CourseThemeInfo(
                                id = "Level C2 srp $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ),
        CourseDetails(
            id = "a1b35beb-f621-46e9-b957-e8d83ace1f92",
            cover = "https://upload.wikimedia.org/wikipedia/en/1/12/Flag_of_Poland.svg",
            name = "Polish language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 pl",
                        name = "Level A1",
                    ),
                    content = polishCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 pl $index",
                            info = CourseThemeInfo(
                                id = "Level A1 pl $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 pl",
                        name = "Level A2",
                    ),
                    content = polishCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 pl $index",
                            info = CourseThemeInfo(
                                id = "Level A2 pl $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 pl",
                        name = "Level B1",
                    ),
                    content = polishCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 pl $index",
                            info = CourseThemeInfo(
                                id = "Level B1 pl $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 pl",
                        name = "Level B2",
                    ),
                    content = polishCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 pl $index",
                            info = CourseThemeInfo(
                                id = "Level B2 pl $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 pl",
                        name = "Level C1",
                    ),
                    content = polishCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 pl $index",
                            info = CourseThemeInfo(
                                id = "Level C1  pl$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 pl",
                        name = "Level C2",
                    ),
                    content = polishCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 pl $index",
                            info = CourseThemeInfo(
                                id = "Level C2 pl $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        ), CourseDetails(
            id = "337732ea-214c-45b3-8d73-2e1b7b67bfa6",
            cover = "https://upload.wikimedia.org/wikipedia/commons/5/5c/Flag_of_Greece.svg",
            name = "Greek language. AI course",
            accessStatus = CourseAccessStatus.UserCourse,
            completionStatus = CourseCompletionStatus.InProgress,
            sections = listOf(
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A1 1 el",
                        name = "Level A1",
                    ),
                    content = greekCourseA1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A1 el $index",
                            info = CourseThemeInfo(
                                id = "Level A1 el $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level A2 el",
                        name = "Level A2",
                    ),
                    content = greekCourseA2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level A2 el $index",
                            info = CourseThemeInfo(
                                id = "Level A2 el $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B1 el",
                        name = "Level B1",
                    ),
                    content = greekCourseB1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B1 el $index",
                            info = CourseThemeInfo(
                                id = "Level B1 el $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level B2 el",
                        name = "Level B2",
                    ),
                    content = greekCourseB2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level B2 el $index",
                            info = CourseThemeInfo(
                                id = "Level B2 el $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C1 el",
                        name = "Level C1",
                    ),
                    content = greekCourseC1Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C1 el $index",
                            info = CourseThemeInfo(
                                id = "Level C1  el$index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
                CourseSection.Default(
                    info = CourseSectionInfo(
                        id = "Level C2 el",
                        name = "Level C2",
                    ),
                    content = greekCourseC2Content().mapIndexed { index, value ->
                        CourseContent.Theme(
                            id = "Level C2 2 el $index",
                            info = CourseThemeInfo(
                                id = "Level C2 el $index",
                                name = value,
                                completionStatus = CourseThemeCompletionStatus.Available,
                            )
                        )
                    }
                ),
            )
        )
    )

    fun germanCourseC2Content() = listOf(
        "Mastery of all verb tenses (including nuanced shifts between Plusquamperfekt, Futur II, conditional forms in narrative and argumentation)",
        "Full range of Passive Voice (active-passive transformations, double passive, passive with modal verbs, passive with sein/werden/worden)",
        "Konjunktiv I and II (stylistic flexibility, alternative forms in reported speech, complex hypotheticals, literary use)",
        "Complex conditional sentences (mixed tenses, nested conditions, highly idiomatic forms)",
        "Advanced participial constructions (stylistic participles in literary and academic texts, absolute participial phrases)",
        "Advanced nominalization (dense nominal chains, legal/academic style, abstract concepts)",
        "Elliptical and fragmented structures (for stylistic effect, colloquial and literary ellipses)",
        "Complex multi-clause structures (layered subordinate clauses, insertions, interruptions, indirect speech within indirect speech)",
        "Stylistic inversion (rare patterns: “Wohl kaum hatte er begonnen, als...”)",
        "Modal particles (full range, subtle meanings in formal, informal, and literary registers: ja, doch, eben, wohl, schon, allerdings, gar, nun mal, ruhig)",
        "Idiomatic, figurative, and metaphorical language (proverbs, sayings, idioms, stylistic devices)",
        "Impersonal expressions (high frequency and variety in formal and academic style: es ist anzunehmen, es gilt zu beachten)",
        "Sophisticated connectors and discourse markers (ungeachtet dessen, indes, wohingegen, insofern, zumal, dennoch, gleichwohl)",
        "Stylistic and register variation (formal, academic, colloquial, poetic, bureaucratic, journalistic)",
        "Rhetorical devices (parallelism, chiasmus, anaphora, allusion)",
        "Use of archaic, regional, and literary structures (optional at C2)",
        "Mastery of stylistic nuance in word order (for emphasis, rhythm, or tone)",
        "Precision in expressing assumptions, speculation, probability (mag sein, dürfte, könnte wohl, sollte, möchte)",
        "High-level nominal and participial adjectives (e.g. der zu verurteilende Angeklagte)",
        "Paraphrasing and reformulating complex ideas (with stylistic variation)",
    )

    fun germanCourseC1Content() = listOf(
        "All verb tenses (including nuanced use of Future II, Plusquamperfekt in complex contexts)",
        "Passive Voice (all tenses, with modal verbs, double passive)",
        "Konjunktiv I (full use in indirect speech, media reports, academic writing)",
        "Konjunktiv II (advanced hypothetical expressions, wishes, regrets, politeness, alternatives to würde)",
        "Conditional Clauses (complex, nested conditions, mixed tenses in conditions)",
        "Nominalization (advanced style, dense nominal structures in academic/formal texts)",
        "Participial Constructions (Partizip I, II — expanded and stylistically varied use, passive/active nuances)",
        "Extended Infinitive Clauses (um...zu, ohne...zu, anstatt...zu, dabei...zu — complex combinations)",
        "Extended subordinate clauses (multiple nested clauses, complex connectors)",
        "Complex relative clauses (with full range of prepositions, abstract nouns, clauses as antecedents)",
        "Word order in highly complex sentences (multiple subordinate clauses, inserted elements)",
        "Modal Particles (fine distinctions in meaning: eben, nun mal, freilich, schon, allerdings)",
        "Elliptical structures (stylistic omissions, incomplete sentences, implied elements)",
        "Stylistic inversion (e.g. “Kaum hatte er das gesagt, da ...”)",
        "Expressing cause, effect, concession, contrast (differentiation of connectors: obgleich, wenngleich, indes, wohingegen, während)",
        "Complex connectors and compound conjunctions (sowohl...als auch, nicht nur...sondern auch, je...desto, einerseits...andererseits)",
        "Advanced prepositional phrases (angesichts, aufgrund, hinsichtlich, im Hinblick auf)",
        "Advanced expressions of probability, assumptions, and speculation (mag sein, dürfte, könnte wohl)",
        "Formal and academic style (impersonal constructions, nominal style)",
        "Reported speech (variety of introductory verbs, complex transformations)",
        "Idiomatic and figurative expressions (wider range of idioms, proverbs)",
        "Stylistic variations (formal, academic, colloquial, literary styles)",
    )

    fun germanCourseB2Content() = listOf(
        "Präteritum (regular use in written and formal texts, narrative style)",
        "Plusquamperfekt (expanded use in storytelling and reports)",
        "Future I and II (expanded use, including assumptions and probability)",
        "Passive Voice (all tenses, including modal verbs + passive)",
        "Konjunktiv I (reported/indirect speech — full use)",
        "Konjunktiv II (hypothetical statements, polite forms, wishes, advice, unreality in past and present)",
        "Conditional Clauses (real, unreal, impossible conditions in present and past)",
        "Extended Subordinate Clauses (je ... desto, indem, soweit, sofern, obgleich, wenngleich)",
        "Complex Relative Clauses (with prepositions, cases, and noun phrases)",
        "Extended Infinitive Clauses (um...zu, ohne...zu, anstatt...zu, damit, als ob, als wenn)",
        "Nominalization (advanced use for formal and academic style)",
        "Participles as adjectives (Partizip I and II, passive and active meaning)",
        "Adjective declension (complex structures, multiple adjectives)",
        "Verbs with prepositions (wider range of abstract and idiomatic combinations)",
        "Prepositional phrases (advanced structures: hinsichtlich, bezüglich, zwecks, etc.)",
        "Word order in complex sentences (multiple subordinate clauses, insertion of clauses)",
        "Modal particles (expanded variety: ja, doch, wohl, schon, ruhig, eigentlich)",
        "Complex conjunctions and connectors (zwar...aber, nicht nur...sondern auch, weder...noch, entweder...oder)",
        "Elliptical structures (omission of elements in complex sentences)",
        "Participial constructions (Partizipialkonstruktionen)",
        "Cause-effect structures (dadurch, infolgedessen, somit, daher)",
        "Expressing concessions (obwohl, obgleich, wenngleich, trotzdem, dennoch — nuanced use)",
        "Expressing opinions, arguments, counter-arguments (structured formal language)",
    )

    fun germanCourseB1Content() = listOf(
        "Präteritum (wider range of verbs, especially in narrative and reports)",
        "Perfekt vs. Präteritum (use in spoken vs. written language)",
        "Plusquamperfekt (past perfect tense)",
        "Future I (werden + infinitive – expanded use)",
        "Future II (introduction, recognition)",
        "Passive Voice (Präsens, Präteritum, Perfekt)",
        "Reflexive Verbs (expanded use with more idiomatic expressions)",
        "Relative Clauses (more complex structures, with all cases and prepositions)",
        "Subordinate Clauses (weil, dass, wenn, obwohl, nachdem, bevor, damit, sodass, etc.)",
        "Indirect Speech (Konjunktiv I – introduction)",
        "Konjunktiv II (polite requests, unreal conditions: hätte, wäre, würde + infinitive)",
        "Conditional Sentences (wenn-Sätze, real and unreal conditions)",
        "Participles as Adjectives (Partizip I & II)",
        "Nominalization of Verbs and Adjectives (basic use)",
        "Adjective Declension (after indefinite, definite, and no article)",
        "Extended Use of Two-Way Prepositions (with abstract meanings)",
        "Prepositional Phrases (expanded set: im Vergleich zu, im Gegensatz zu, etc.)",
        "Verbs with Fixed Prepositions (wider range)",
        "More complex word order in main and subordinate clauses",
        "Infinitive Clauses with zu (expanded use, ohne...zu, statt...zu)",
        "Extended use of modal particles (halt, eben, wohl, schon)",
        "Idiomatic expressions and collocations (common at B1 level)",
        "Expressing opinions, advantages/disadvantages (structured expressions)",
        "Expressing purpose, reason, result (damit, um...zu, deshalb, deswegen)",
        "Expressing contrast (trotzdem, jedoch, dennoch)",
    )

    fun germanCourseA2Content() = listOf(
        "Present Tense (review and extension with irregular verbs)",
        "Perfekt Tense (more irregular verbs, word order with time expressions)",
        "Präteritum (introduction, mainly with modal verbs and common verbs like war, hatte)",
        "Future Tense (werden + infinitive)",
        "Nominative, Accusative, Dative Cases (extended use)",
        "Genitive Case (basic introduction)",
        "Dative and Accusative Prepositions (expanded use)",
        "Two-way Prepositions with Case Choice (accusative for movement, dative for position)",
        "Adjective Endings (all cases – nominative, accusative, dative)",
        "Comparative and Superlative Forms (expanded use)",
        "Pronouns (reflexive in accusative and dative, demonstrative, relative – introduction)",
        "Relative Clauses (with der, die, das)",
        "Subordinate Clauses (weil, dass, wenn, obwohl, bevor, nachdem, etc.)",
        "Indirect Questions (Ich weiß nicht, ob... / wann...)",
        "Infinitive Clauses with “zu” (Ich habe vor, ... zu ...)",
        "Passive Voice (present tense – basic introduction)",
        "Imperative (formal, informal singular and plural)",
        "Word Order in Subordinate and Main Clauses (review and more complex sentences)",
        "Modal Particles (doch, mal, eben – introduction)",
        "Expressions with “es” (es gibt, es ist, es war)",
        "Verbs with Fixed Prepositions (warten auf, sich freuen über, etc.)",
        "Use of “man” (general statements)",
        "Time Expressions (seit, vor, während)",
        "Expressing Opinions (Ich finde, dass... / Meiner Meinung nach...)",
        "Expressing Conditions (wenn-Sätze, conditional statements)",
    )

    fun germanCourseA1Content() = listOf(
        "Verb Conjugation (Regular & Irregular in Present Tense)",
        "Separable Verbs",
        "Reflexive Verbs (basic use)",
        "Modal Verbs (expanded use)",
        "Word Order in Main Clauses",
        "Word Order in Questions",
        "Word Order with Modal and Separable Verbs",
        "Sentence Connectors (weil, dass – basic introduction)",
        "Negation with “nicht” and “kein” (extended use)",
        "Nominative, Accusative, and Dative Cases (introduction)",
        "Personal Pronouns in Accusative and Dative",
        "Possessive Pronouns in All Cases",
        "Definite and Indefinite Articles in All Cases",
        "Adjective Endings (nominative and accusative)",
        "Prepositions with Accusative (durch, für, ohne, etc.)",
        "Prepositions with Dative (mit, bei, nach, etc.)",
        "Two-way Prepositions (in, an, auf – basic use)",
        "Time Expressions (am, um, im, von...bis)",
        "Past Tense – Perfekt (with haben and sein, basic verbs)",
        "Verb Placement in Subordinate Clauses",
        "Subordinating Conjunctions (weil, dass, wenn – basic)",
        "Simple Future Tense (werden + infinitive – optional/intro)",
        "Comparatives and Superlatives (basic adjectives)",
        "Demonstrative Pronouns (dieser, jener – basic use)",
        "Indefinite Pronouns (jemand, niemand, man)",
        "Question Words (wann, warum, wie lange, etc.)",
        "Asking and Giving Directions",
        "Describing People, Objects, and Places",
        "Telling Time, Dates, and Schedules",
        "Expressing Likes and Dislikes (gern, lieber, am liebsten)",
        "Everyday Conversations and Politeness Forms",
    )

    fun germanCourseA0Content() = listOf(
        "German Alphabet and Pronunciation",
        "Nouns and Gender (der, die, das)",
        "Definite Articles (der, die, das)",
        "Indefinite Articles (ein, eine)",
        "Plural Forms of Nouns",
        "Personal Pronouns (ich, du, er, sie, es, etc.)",
        "Verb Conjugation in Present Tense (Regular Verbs)",
        "Common Irregular Verbs (sein, haben, etc.)",
        "Sentence Structure (Basic Word Order: SVO)",
        "Yes/No Questions (Ja/Nein-Fragen)",
        "W-Questions (Wer, Was, Wo, etc.)",
        "Negation with \"nicht\" and \"kein\"",
        "Possessive Pronouns (mein, dein, etc.)",
        "Modal Verbs (dürfen, können, müssen, etc. - basic use)",
        "Accusative Case (direct objects)",
        "Nominative vs. Accusative Articles",
        "Common Prepositions (mit, in, auf, etc. - basic use)",
        "Numbers and Counting",
        "Telling Time",
        "Days, Months, Seasons",
        "Greetings and Introductions",
        "Formal vs. Informal Address (du vs. Sie)",
        "Common Adjectives (basic form)",
        "Adjective Placement (before noun)",
        "Imperative (basic commands)",
        "Simple Conjunctions (und, oder, aber)",
    )


    fun bulgarianCourseA1Content() = listOf(
        "болгарский алфавит и произношение",
        "личные местоимения (аз, ти, той, тя, то, ние, вие, те)",
        "настоящее время правильных глаголов",
        "глагол „съм“ в настоящем времени",
        "род существительных (мужской, женский, средний)",
        "определённый артикль (постпозитивный -ът, -та, -то, -те)",
        "множественное число существительных",
        "прилагательные и согласование с существительными",
        "притяжательные местоимения (мой, твой, негов, нейн, наш, ваш)",
        "вопросительные слова (кой, какво, къде, кога, защо, как)",
        "отрицание с частицей „не“",
        "вопросы да/нет (интонация, „ли“)",
        "количественные числительные (1–100)",
        "простые предлоги (в, на, под, над, с)",
        "простые союзы (и, но, защото, или)",
        "выражение времени и дат",
        "модальные глаголы (мога, трябва, искам)",
        "императив в ед. числе (повелительное наклонение)"
    )

    fun bulgarianCourseA2Content() = listOf(
        "аорист (прошедшее время) правильных глаголов",
        "аорист глагола „съм“",
        "будущее время с „ще“",
        "возвратные глаголы и частица „се“",
        "расширенные формы притяжательных местоимений",
        "плюсквамперфект (бях + мин. причастие)",
        "вид глагола (совершенный и несовершенный)",
        "отрицание с никой, нищо, никога",
        "сравнительная и превосходная степень прилагательных",
        "личные и безличные формы модальных глаголов",
        "предлоги времени и места (до, от, през, между)",
        "условные предложения с „ако“ (тип 1)",
        "сложные предложения с „че“, „когато“, „докато“",
        "порядковые числительные (първи, втори, трети)"
    )

    fun bulgarianCourseB1Content() = listOf(
        "имперфект (несвършен минал вид)",
        "перфект (съм + мин. причастие)",
        "будущее в прошедшем (щях да)",
        "сослагательное наклонение с „да“ + глагол",
        "косвенная речь с „че“ и „да“",
        "сложные возвратные конструкции",
        "двойные местоименные конструкции (го го, им го)",
        "условные предложения типов 1 и 2",
        "причастия и деепричастия (настоящее, прошедшее)",
        "наречия частотности, образа действия, степени",
        "сравнительные конструкции (колкото, както)",
        "вопросы в косвенной речи",
        "модальные глаголы в прошедшем и будущем",
        "страдательный залог с „се“ и с причастиями"
    )

    fun bulgarianCourseB2Content() = listOf(
        "условные предложения типа 3",
        "согласование времён в косвенной речи",
        "эмфатические конструкции и порядок слов",
        "безличные конструкции (става, има, няма, трябва)",
        "вариации порядка слов для акцента и противопоставления",
        "расширенное сослагательное наклонение и модальность",
        "стилистическое использование причастий",
        "тонкие различия между совершенным и несовершенным видом",
        "сложные союзы (въпреки че, макар че)",
        "усложнённые формы отрицания",
        "регистровая вариативность (разговорный vs официальный стиль)",
        "устойчивые грамматические выражения и идиомы",
        "страдательный залог в разных временах"
    )

    fun bulgarianCourseC1Content() = listOf(
        "стилистическое использование вида и времени",
        "эллипсис и акцент в повествовательных текстах",
        "риторическое и литературное сослагательное наклонение",
        "вложенные придаточные предложения",
        "стилистическая инверсия и прагматический порядок слов",
        "грамматическая связность в академических и публицистических текстах",
        "сложные уступительные конструкции (дори и да, колкото и да)",
        "дискурсивные маркеры для аргументации",
        "тонкие грамматические средства модальности и оценки",
        "интеграция идиом и устойчивых выражений в грамматике"
    )

    fun bulgarianCourseC2Content() = listOf(
        "полный контроль над временными и видовыми оттенками",
        "умелое переключение между стилями и регистрами",
        "грамматические средства риторики и высокой стилистики",
        "эллипсис, уплотнение высказывания и компрессия текста",
        "манипуляция синтаксисом для акцента и иронии",
        "средства связности высокого уровня",
        "морфосинтаксическая вариативность и редкие формы",
        "грамматические приёмы убеждения, противопоставления, контраста",
        "внедрение диалектных и архаических форм (при необходимости)",
        "грамматическая интерпретация текстов высокого стиля (литература, политика, наука)"
    )

    fun italianCourseA1Content() = listOf(
        "italian alphabet and pronunciation",
        "definite articles",
        "indefinite articles",
        "gender of nouns",
        "plural forms of nouns",
        "personal pronouns (subject)",
        "present tense of regular verbs -are",
        "present tense of regular verbs -ere",
        "present tense of regular verbs -ire",
        "present tense of essere",
        "present tense of avere",
        "present tense of andare",
        "present tense of fare",
        "present tense of stare",
        "subject-verb agreement in present tense",
        "negation with non",
        "forming simple yes/no questions",
        "question words (chi, che cosa, dove, quando, come, perché)",
        "possessive adjectives (il mio, la tua, ecc.)",
        "demonstrative adjectives (questo, quello)",
        "basic prepositions (di, a, da, in, con, su, per, tra/fra)",
        "agreement of adjectives with nouns (gender and number)",
        "cardinal numbers (1-100)",
        "ordinal numbers (primo, secondo, ecc.)",
        "telling time (hours and minutes)",
        "days of the week",
        "months of the year",
        "simple conjunctions (e, ma, o, perché)",
        "basic imperative (tu form affirmative)",
        "passato prossimo introduction (regular verbs)",
        "basic greetings and introductions phrases"
    )

    fun italianCourseA2Content() = listOf(
        "imperfetto (imperfect tense)",
        "futuro semplice (simple future tense)",
        "reflexive verbs in present tense",
        "direct object pronouns (mi, ti, lo, la, ci, vi, li, le)",
        "indirect object pronouns (mi, ti, gli, le, ci, vi, loro)",
        "partitive articles (del, dello, della, dei, degli, delle)",
        "comparatives of adjectives and adverbs",
        "superlatives (absolute and relative)",
        "adverbs of manner and degree",
        "prepositions of place (a, in, su, sotto, davanti a, ecc.)",
        "prepositions of time (da, fino a, prima di, dopo)",
        "negation with niente and nessuno",
        "modal verbs in present tense (potere, dovere, volere)",
        "simple conditional mood present (condizionale presente)",
        "indefinite pronouns (qualcuno, qualcosa, nessuno)",
        "relative pronouns che and cui",
        "basic conjunctions for cause and consequence (perché, quindi, perché)",
        "negative imperative (non + infinitive)",
        "use of articles with prepositions (al, nel, dello, ecc.)"
    )

    fun italianCourseB1Content() = listOf(
        "passato prossimo irregular verbs",
        "trapassato prossimo (past perfect)",
        "imperfetto vs passato prossimo contrast",
        "past conditional (condizionale passato)",
        "future perfect (futuro anteriore)",
        "reflexive verbs in passato prossimo",
        "present subjunctive (congiuntivo presente) introduction",
        "imperfect subjunctive (congiuntivo imperfetto) introduction",
        "complex relative pronouns (il quale, la quale, i quali)",
        "indirect speech (discorso indiretto)",
        "passive voice present and passato prossimo",
        "gerundio (gerund) usage in progressive tenses",
        "modal verbs in past tenses",
        "causative construction (fare + infinitive)",
        "double object pronouns combinations",
        "expressing hypotheses (second conditional)",
        "advanced conjunctions (sebbene, nonostante, benché)",
        "pronominal verbs and reflexive constructions advanced use",
        "reported questions"
    )

    fun italianCourseB2Content() = listOf(
        "subjunctive mood advanced use in present, past and imperfect",
        "conditional mood full use for politeness and hypothetical situations",
        "future perfect and conditional perfect detailed use",
        "passive voice with all tenses and modals",
        "complex relative clauses with prepositions",
        "indirect speech with changes in tense, pronouns, and mood",
        "advanced pronominal verb constructions",
        "advanced use of gerunds, participles, and infinitives",
        "causative constructions in complex forms",
        "nuances of modal verbs (doubt, probability, permission)",
        "advanced use of negation (double negation, litotes)",
        "complex sentence linking with subordinators and connectors",
        "expressions of cause, purpose, concession, contrast in complex sentences",
        "emphatic and formal imperatives",
        "idiomatic expressions and figurative language grammar",
        "discourse markers for cohesion and coherence"
    )

    fun italianCourseC1Content() = listOf(
        "complex subjunctive usage in literary and formal registers",
        "advanced conditional with multiple clauses and nuances",
        "passive voice in literary and academic styles",
        "inversion and emphasis in sentence construction",
        "pronominal verbs and reflexives in stylistic contexts",
        "advanced idiomatic and proverbial grammar structures",
        "ellipsis, anaphora, and cataphora in extended texts",
        "stylistic distinctions between registers (formal, informal, literary)",
        "refined negation (litotes, emphatic, rhetorical)",
        "complex connectors for concession, cause, result, contrast",
        "nominalizations and abstract grammatical forms",
        "advanced tense and aspect distinctions in narrative and descriptive text",
        "discourse topic management and cohesion in long texts",
        "integration of grammatical and pragmatic competence"
    )

    fun italianCourseC2Content() = listOf(
        "mastery of all subjunctive tenses in nuanced contexts",
        "rhetorical and stylistic devices in grammar",
        "advanced syntactic manipulation for emphasis and rhythm",
        "extended use of nominalizations and abstract expressions",
        "intricate modality expressions of certainty, doubt, politeness",
        "advanced pronominal systems including emphatic and disjunctive pronouns",
        "mastery of ellipsis, anaphora, cataphora in complex discourse",
        "handling archaic, poetic, and regional grammatical variants",
        "advanced connective structures for organizing extended discourse",
        "precise tense and aspect use for narrative subtleties",
        "mastery of passive, causative, and periphrastic constructions",
        "integration of grammar with sociolinguistic competence",
        "genre and style specific grammatical mastery"
    )

    fun spanishCourseA1Content() = listOf(
        "spanish alphabet and pronunciation",
        "definite articles (el, la, los, las)",
        "indefinite articles (un, una, unos, unas)",
        "gender of nouns",
        "plural forms of nouns",
        "subject personal pronouns",
        "present tense of regular verbs -ar",
        "present tense of regular verbs -er",
        "present tense of regular verbs -ir",
        "present tense of ser",
        "present tense of estar",
        "present tense of tener",
        "present tense of ir",
        "present tense of hacer",
        "subject-verb agreement in present tense",
        "negation with no",
        "forming simple yes/no questions",
        "question words (qué, quién, dónde, cuándo, cómo, por qué)",
        "possessive adjectives (mi, tu, su, nuestro, vuestro)",
        "demonstrative adjectives (este, ese, aquel)",
        "basic prepositions (de, a, en, con, por, para, sin, sobre)",
        "agreement of adjectives with nouns (gender and number)",
        "cardinal numbers (1–100)",
        "ordinal numbers (primero, segundo, tercero)",
        "telling time (hours and minutes)",
        "days of the week",
        "months of the year",
        "simple conjunctions (y, pero, o, porque)",
        "basic imperative (tú affirmative form)",
        "pretérito perfecto simple introduction (regular verbs)",
        "basic greetings and introductions phrases"
    )

    fun spanishCourseA2Content() = listOf(
        "pretérito imperfecto (imperfect tense)",
        "futuro simple (simple future tense)",
        "reflexive verbs in present tense",
        "direct object pronouns (me, te, lo, la, nos, os, los, las)",
        "indirect object pronouns (me, te, le, nos, os, les)",
        "partitive expressions with de (un poco de, algo de)",
        "comparatives of adjectives and adverbs",
        "superlatives (absolute and relative)",
        "adverbs of manner and degree",
        "prepositions of place (a, en, sobre, bajo, delante de, detrás de)",
        "prepositions of time (desde, hasta, antes de, después de)",
        "negation with nadie and nada",
        "modal verbs in present tense (poder, deber, querer)",
        "simple conditional mood present (condicional simple)",
        "indefinite pronouns (alguien, algo, nadie)",
        "relative pronouns que and quien",
        "basic conjunctions for cause and consequence (porque, entonces, por eso)",
        "negative imperative (no + subjunctive)",
        "use of articles with prepositions (al, del)"
    )

    fun spanishCourseB1Content() = listOf(
        "pretérito perfecto compuesto irregular verbs",
        "pretérito pluscuamperfecto (past perfect)",
        "pretérito imperfecto vs pretérito perfecto simple contrast",
        "condicional compuesto (past conditional)",
        "futuro perfecto (future perfect)",
        "reflexive verbs in pretérito perfecto compuesto",
        "present subjunctive (presente de subjuntivo) introduction",
        "imperfect subjunctive (imperfecto de subjuntivo) introduction",
        "complex relative pronouns (el cual, la cual, los cuales)",
        "indirect speech (estilo indirecto)",
        "passive voice present and pretérito perfecto compuesto",
        "gerundio (gerund) usage in progressive tenses",
        "modal verbs in past tenses",
        "causative construction (hacer + infinitive)",
        "double object pronouns combinations",
        "expressing hypotheses (second conditional)",
        "advanced conjunctions (aunque, a pesar de que, pese a que)",
        "pronominal verbs and reflexive constructions advanced use",
        "reported questions"
    )

    fun spanishCourseB2Content() = listOf(
        "subjunctive mood advanced use in present, past and imperfect",
        "conditional mood full use for politeness and hypothetical situations",
        "future perfect and conditional perfect detailed use",
        "passive voice with all tenses and modals",
        "complex relative clauses with prepositions",
        "indirect speech with changes in tense, pronouns, and mood",
        "advanced pronominal verb constructions",
        "advanced use of gerunds, participles, and infinitives",
        "causative constructions in complex forms",
        "nuances of modal verbs (doubt, probability, permission)",
        "advanced use of negation (double negation, litotes)",
        "complex sentence linking with subordinators and connectors",
        "expressions of cause, purpose, concession, contrast in complex sentences",
        "emphatic and formal imperatives",
        "idiomatic expressions and figurative language grammar",
        "discourse markers for cohesion and coherence"
    )

    fun spanishCourseC1Content() = listOf(
        "complex subjunctive usage in literary and formal registers",
        "advanced conditional with multiple clauses and nuances",
        "passive voice in literary and academic styles",
        "inversion and emphasis in sentence construction",
        "pronominal verbs and reflexives in stylistic contexts",
        "advanced idiomatic and proverbial grammar structures",
        "ellipsis, anaphora, and cataphora in extended texts",
        "stylistic distinctions between registers (formal, informal, literary)",
        "refined negation (litotes, emphatic, rhetorical)",
        "complex connectors for concession, cause, result, contrast",
        "nominalizations and abstract grammatical forms",
        "advanced tense and aspect distinctions in narrative and descriptive text",
        "discourse topic management and cohesion in long texts",
        "integration of grammatical and pragmatic competence"
    )

    fun spanishCourseC2Content() = listOf(
        "mastery of all subjunctive tenses in nuanced contexts",
        "rhetorical and stylistic devices in grammar",
        "advanced syntactic manipulation for emphasis and rhythm",
        "extended use of nominalizations and abstract expressions",
        "intricate modality expressions of certainty, doubt, politeness",
        "advanced pronominal systems including emphatic and disjunctive pronouns",
        "mastery of ellipsis, anaphora, cataphora in complex discourse",
        "handling archaic, poetic, and regional grammatical variants",
        "advanced connective structures for organizing extended discourse",
        "precise tense and aspect use for narrative subtleties",
        "mastery of passive, causative, and periphrastic constructions",
        "integration of grammar with sociolinguistic competence",
        "genre and style specific grammatical mastery"
    )

    fun frenchCourseA1Content() = listOf(
        "french alphabet and pronunciation",
        "definite articles (le, la, les)",
        "indefinite articles (un, une, des)",
        "gender of nouns",
        "plural forms of nouns",
        "subject personal pronouns",
        "present tense of regular verbs -er",
        "present tense of regular verbs -ir",
        "present tense of regular verbs -re",
        "present tense of être",
        "present tense of avoir",
        "present tense of aller",
        "present tense of faire",
        "subject-verb agreement in present tense",
        "negation with ne...pas",
        "forming simple yes/no questions",
        "question words (qui, que, où, quand, comment, pourquoi)",
        "possessive adjectives (mon, ton, son, notre, votre, leur)",
        "demonstrative adjectives (ce, cette, ces)",
        "basic prepositions (de, à, dans, avec, sur, pour, par, sans)",
        "agreement of adjectives with nouns (gender and number)",
        "cardinal numbers (1–100)",
        "ordinal numbers (premier, deuxième, troisième)",
        "telling time (hours and minutes)",
        "days of the week",
        "months of the year",
        "simple conjunctions (et, mais, ou, parce que)",
        "basic imperative (tu affirmative form)",
        "passé composé introduction (regular verbs)",
        "basic greetings and introductions phrases"
    )

    fun frenchCourseA2Content() = listOf(
        "imparfait (imperfect tense)",
        "futur simple (simple future tense)",
        "reflexive verbs in present tense",
        "direct object pronouns (me, te, le, la, nous, vous, les)",
        "indirect object pronouns (me, te, lui, nous, vous, leur)",
        "partitive articles (du, de la, des)",
        "comparatives of adjectives and adverbs",
        "superlatives (absolute and relative)",
        "adverbs of manner and degree",
        "prepositions of place (à, dans, sur, sous, devant, derrière)",
        "prepositions of time (depuis, jusqu’à, avant, après)",
        "negation with rien and personne",
        "modal verbs in present tense (pouvoir, devoir, vouloir)",
        "simple conditional mood present (conditionnel présent)",
        "indefinite pronouns (quelqu’un, quelque chose, personne)",
        "relative pronouns qui and que",
        "basic conjunctions for cause and consequence (parce que, donc, car)",
        "negative imperative (ne...pas + infinitive)",
        "use of articles with prepositions (au, aux, du)"
    )

    fun frenchCourseB1Content() = listOf(
        "passé composé irregular verbs",
        "plus-que-parfait (past perfect)",
        "imparfait vs passé composé contrast",
        "conditionnel passé (past conditional)",
        "futur antérieur (future perfect)",
        "reflexive verbs in passé composé",
        "present subjunctive (subjonctif présent) introduction",
        "imperfect subjunctive (subjonctif imparfait) introduction",
        "complex relative pronouns (lequel, laquelle, lesquels)",
        "indirect speech (discours indirect)",
        "passive voice present and passé composé",
        "gérondif usage in progressive tenses",
        "modal verbs in past tenses",
        "causative construction (faire + infinitive)",
        "double object pronouns combinations",
        "expressing hypotheses (second conditional)",
        "advanced conjunctions (bien que, quoique, malgré que)",
        "pronominal verbs and reflexive constructions advanced use",
        "reported questions"
    )

    fun frenchCourseB2Content() = listOf(
        "subjunctive mood advanced use in present, past and imperfect",
        "conditional mood full use for politeness and hypothetical situations",
        "future perfect and conditional perfect detailed use",
        "passive voice with all tenses and modals",
        "complex relative clauses with prepositions",
        "indirect speech with changes in tense, pronouns, and mood",
        "advanced pronominal verb constructions",
        "advanced use of gerunds, participles, and infinitives",
        "causative constructions in complex forms",
        "nuances of modal verbs (doubt, probability, permission)",
        "advanced use of negation (double negation, litotes)",
        "complex sentence linking with subordinators and connectors",
        "expressions of cause, purpose, concession, contrast in complex sentences",
        "emphatic and formal imperatives",
        "idiomatic expressions and figurative language grammar",
        "discourse markers for cohesion and coherence"
    )

    fun frenchCourseC1Content() = listOf(
        "complex subjunctive usage in literary and formal registers",
        "advanced conditional with multiple clauses and nuances",
        "passive voice in literary and academic styles",
        "inversion and emphasis in sentence construction",
        "pronominal verbs and reflexives in stylistic contexts",
        "advanced idiomatic and proverbial grammar structures",
        "ellipsis, anaphora, and cataphora in extended texts",
        "stylistic distinctions between registers (formal, informal, literary)",
        "refined negation (litotes, emphatic, rhetorical)",
        "complex connectors for concession, cause, result, contrast",
        "nominalizations and abstract grammatical forms",
        "advanced tense and aspect distinctions in narrative and descriptive text",
        "discourse topic management and cohesion in long texts",
        "integration of grammatical and pragmatic competence"
    )

    fun frenchCourseC2Content() = listOf(
        "mastery of all subjunctive tenses in nuanced contexts",
        "rhetorical and stylistic devices in grammar",
        "advanced syntactic manipulation for emphasis and rhythm",
        "extended use of nominalizations and abstract expressions",
        "intricate modality expressions of certainty, doubt, politeness",
        "advanced pronominal systems including emphatic and disjunctive pronouns",
        "mastery of ellipsis, anaphora, cataphora in complex discourse",
        "handling archaic, poetic, and regional grammatical variants",
        "advanced connective structures for organizing extended discourse",
        "precise tense and aspect use for narrative subtleties",
        "mastery of passive, causative, and periphrastic constructions",
        "integration of grammar with sociolinguistic competence",
        "genre and style specific grammatical mastery"
    )

    fun ancientGreekCourseA1Content() = listOf(
        "ancient greek alphabet and pronunciation",
        "noun cases overview (nominative, genitive, dative, accusative, vocative)",
        "first declension nouns (α- и η-тип)",
        "second declension nouns (ο-тип)",
        "personal pronouns (1st and 2nd person singular and plural)",
        "present indicative active of first conjugation verbs (-ω verbs)",
        "present indicative active of second conjugation verbs",
        "present indicative active of irregular verbs (εἰμί, ἔχω)",
        "subject-verb agreement basics",
        "article usage (definite article only)",
        "basic negation with οὐ and μή",
        "forming yes/no questions",
        "question words (τίς, τί, πῶς, ποῦ, πότε)",
        "present imperative active",
        "basic prepositions with accusative and genitive",
        "adjective agreement in gender, number, and case",
        "simple conjunctions (καί, δέ, ἀλλά)"
    )

    fun ancientGreekCourseA2Content() = listOf(
        "third declension nouns and adjectives",
        "middle and passive voice present indicative active forms",
        "aorist indicative active (first aorist)",
        "imperfect indicative active",
        "future indicative active",
        "personal pronouns in all cases",
        "demonstrative pronouns and adjectives",
        "genitive absolute construction introduction",
        "relative pronouns (ὅς, ἥ, ὅ)",
        "indirect statements with infinitive",
        "negative commands with μή",
        "comparative and superlative forms of adjectives",
        "expressing time and place with prepositions and cases",
        "second aorist forms introduction",
        "present subjunctive active forms",
        "basic use of optative mood",
        "dual number forms introduction"
    )

    fun ancientGreekCourseB1Content() = listOf(
        "aorist indicative passive",
        "perfect indicative active and middle",
        "pluperfect indicative active",
        "infinitive forms active and middle/passive",
        "participle forms active, middle, and passive",
        "causal, concessive, and temporal clauses with conjunctions",
        "indirect questions",
        "subjunctive mood in purpose and result clauses",
        "optative mood in wishes and potential statements",
        "conditional sentences types (simple and complex)",
        "use of the augment in past tenses",
        "middle and passive imperative forms",
        "pronouns: reflexive and intensive",
        "indirect discourse (accusative and infinitive)",
        "complex uses of the article"
    )

    fun ancientGreekCourseB2Content() = listOf(
        "advanced uses of subjunctive and optative moods",
        "periphrastic verb forms",
        "conditional sentences with mixed tenses and moods",
        "causative and factitive constructions",
        "advanced participle uses (circumstantial, concessive)",
        "complex relative clauses with antecedents and without",
        "advanced negation forms",
        "temporal and conditional conjunctions",
        "syntax of indirect speech with sequence of moods",
        "use of particles for emphasis and modality",
        "advanced uses of dual number and rare forms",
        "analysis of dialectal variations in grammar",
        "advanced syntax of infinitives and participles"
    )

    fun ancientGreekCourseC1Content() = listOf(
        "literary stylistic devices in syntax and morphology",
        "rhetorical figures using grammatical structures",
        "ellipsis and anaphora in complex texts",
        "use of archaic and poetic forms",
        "fine distinctions in aspect and mood usage",
        "complex nominalizations and abstract formations",
        "advanced discourse cohesion and topic management",
        "syntax in philosophical and historical texts",
        "grammatical nuances in dramatic and lyric poetry",
        "stylistic variation in prose vs poetry"
    )

    fun ancientGreekCourseC2Content() = listOf(
        "mastery of all moods and tenses with subtle distinctions",
        "integration of dialectal and chronological grammar variations",
        "advanced rhetorical constructions and syntax inversion",
        "handling complex subordinate clauses with multiple embeddings",
        "deep understanding of verbal aspect and voice distinctions",
        "precise use of particles and modal expressions",
        "mastery of syntactic ambiguity and disambiguation",
        "comprehensive analysis of grammar in extant classical texts",
        "expertise in reconstruction of unattested grammatical forms",
        "integration of grammatical knowledge with textual interpretation"
    )

    fun chineseCourseA1Content() = listOf(
        "chinese pinyin and tones",
        "basic sentence word order (SVO)",
        "personal pronouns (我, 你, 他/她)",
        "basic negation with 不 and 没",
        "basic question particles 吗 and 呢",
        "measure words introduction (个, 本, 只)",
        "simple nouns and classifiers",
        "basic verbs (是, 有, 去, 来)",
        "present tense time words (现在, 今天)",
        "basic adjectives and their placement",
        "simple sentences with “有” and “没有”",
        "possessive particle 的",
        "basic conjunctions (和, 但是, 因为)",
        "numbers 1–100",
        "time expressions (几点, 星期几, 什么时候)",
        "simple commands and requests",
        "basic greetings and introductions"
    )

    fun chineseCourseA2Content() = listOf(
        "serial verb constructions",
        "coverbs (在, 给, 对)",
        "resultative complements",
        "modal particles (了, 吧, 呢) in simple use",
        "more measure words (辆, 瓶, 杯)",
        "expressing past with 了",
        "expressing future with 要, 会",
        "comparatives (比, 更)",
        "expressing ability with 会",
        "frequency adverbs (经常, 有时候)",
        "questions with 谁, 什么, 哪里, 为什么",
        "prepositional phrases with 在 and 从",
        "basic conjunctions for cause and effect (因为...所以)",
        "aspect particles (过) for experience",
        "basic serial verb complements",
        "simple directional complements",
        "expressing suggestions and commands with 应该, 要不要"
    )

    fun chineseCourseB1Content() = listOf(
        "把 and 被 constructions",
        "complex resultative complements",
        "expressing duration of actions (Verb + 了 + Duration)",
        "complex modal particles (过, 着)",
        "indirect speech structures",
        "expressing opinions with 觉得, 认为",
        "expressing giving and receiving with 给, 叫, 让",
        "more complex serial verb constructions",
        "passive voice nuanced uses",
        "comparisons with 越来越",
        "expressing purpose with 为了",
        "expressing hypothetical situations (如果...就)",
        "expressing ability, permission, and obligation with 可以, 得, 应该",
        "complementing verbs with direction and result",
        "relative clauses with 的",
        "conjunctions expressing contrast and concession (虽然...但是)"
    )

    fun chineseCourseB2Content() = listOf(
        "complex passive constructions",
        "advanced aspect particles usage (着, 了, 过) combined",
        "more subtle modal particle uses (嘛, 呀, 哟)",
        "expressing cause, effect, and purpose with multiple conjunctions",
        "conditional sentences with multiple clauses",
        "complex relative clauses with omission",
        "advanced resultative verb complements",
        "expressing emphasis with 是...的结构",
        "topic-comment sentence structure advanced uses",
        "complex serial verb sequences",
        "expressing polite requests and offers",
        "expressing degrees of certainty and doubt",
        "discourse markers for cohesion and transition",
        "expressions of concession and contrast nuanced forms"
    )

    fun chineseCourseC1Content() = listOf(
        "stylistic particles and rhetorical particles in detail",
        "ellipsis and topic chaining in extended discourse",
        "advanced metaphorical and idiomatic grammar constructions",
        "formal and literary sentence structures",
        "expressing subtle mood and modality in narration",
        "complex sentence embedding and embedding clauses",
        "pragmatic uses of particles for politeness, emphasis, and nuance",
        "advanced nominalization and verb nominalization",
        "manipulating word order for emphasis and style",
        "discourse cohesion and topic management in long texts",
        "expressing irony, sarcasm, and humor grammatically",
        "regional variations in grammar and syntax"
    )

    fun chineseCourseC2Content() = listOf(
        "mastery of all aspect and mood particles in nuanced contexts",
        "expert use of rhetorical and stylistic grammatical devices",
        "handling complex topic chains and ellipsis for textual cohesion",
        "advanced pragmatic functions of grammar in spoken and written language",
        "precise manipulation of modality and evidentiality",
        "high-level discourse structuring and cohesion techniques",
        "handling archaic, classical Chinese grammar forms (if applicable)",
        "integration of grammar with sociolinguistic and cultural competence",
        "expertise in formal, literary, and colloquial registers",
        "fine distinctions in pragmatic particles usage and sequencing"
    )

    fun koreanCourseA1Content() = listOf(
        "korean hangul alphabet and pronunciation",
        "basic sentence word order (SOV)",
        "personal pronouns (저, 나, 당신, 그, 그녀)",
        "subject particles (이/가)",
        "topic particles (은/는)",
        "object particles (을/를)",
        "basic negation with 안 and 못",
        "basic question endings (습니까?, 예요/이에요)",
        "present tense of regular verbs (하다, 가다, 먹다)",
        "basic adjectives and their placement",
        "basic honorific forms (세요, 시-)",
        "possessive particle (의)",
        "simple conjunctions (그리고, 그런데, 하지만)",
        "basic numbers (native and sino-korean)",
        "telling time and date expressions",
        "simple commands and requests (세요)",
        "basic greetings and introductions"
    )

    fun koreanCourseA2Content() = listOf(
        "past tense verb endings (았/었/였어요)",
        "future tense verb endings (겠어요)",
        "informal polite and casual speech levels",
        "imperative and propositive endings (세요, 자)",
        "expressing existence (있다/없다)",
        "particles for location and direction (에, 에서, 으로)",
        "more complex question forms (나요?, 까요?)",
        "conjunctions for cause and effect (그래서, 때문에)",
        "adverbs of frequency and degree",
        "modals (수 있다, 수 없다)",
        "comparatives and superlatives",
        "expressing ability and permission",
        "negation with 지 않다",
        "using -고 to connect verbs and clauses",
        "indirect speech basics",
        "topic-comment sentence structure deeper understanding"
    )

    fun koreanCourseB1Content() = listOf(
        "honorific vocabulary and verb forms",
        "passive voice constructions",
        "causative verb forms",
        "relative clauses with -는 것, -은/는 사람",
        "expressing intentions and plans (려고 하다)",
        "reported speech with -다고 하다",
        "conditional sentences with -(으)면",
        "expressing wishes and hopes (-고 싶다, -았으면 좋겠다)",
        "modal verbs in past and present",
        "adverbial clauses of time and reason",
        "complex conjunctions (비록 -지만, -는데도 불구하고)",
        "expressing necessity and obligation (-야 하다, -어야 하다)",
        "nominalization with -기 and -음/ㅁ",
        "expressions of comparison and contrast",
        "expressing cause and effect with connectors"
    )

    fun koreanCourseB2Content() = listOf(
        "advanced honorifics and politeness levels",
        "advanced relative clauses with embedded verbs",
        "nuanced uses of subjunctive-like expressions (-더라, -나 보다)",
        "expressing reported commands and requests",
        "advanced conjunctions and discourse markers",
        "expressing contrast, concession, and condition in complex sentences",
        "expressions of probability, certainty, and doubt",
        "passive and causative advanced uses",
        "complex nominalizations and verb modifiers",
        "ellipsis and topic chaining in discourse",
        "nuances of negation and emphasis",
        "expressions of modality and politeness"
    )

    fun koreanCourseC1Content() = listOf(
        "stylistic devices in sentence construction",
        "rhetorical questions and indirect speech nuances",
        "advanced sentence inversion and emphasis",
        "ellipsis, anaphora, and cataphora in long texts",
        "use of archaic and literary verb forms",
        "complex discourse cohesion and topic management",
        "register and style variation in formal and informal contexts",
        "advanced modality and evidentiality expressions",
        "expressions of irony, sarcasm, and humor in grammar",
        "nominalizations and abstract grammatical forms in literature"
    )

    fun koreanCourseC2Content() = listOf(
        "mastery of all speech levels and honorific forms in nuanced contexts",
        "expert manipulation of sentence structure for rhetorical effect",
        "handling complex subordinate clauses with multiple embeddings",
        "advanced pragmatic and sociolinguistic grammar use",
        "precise use of modality, evidentiality, and politeness particles",
        "integration of archaic, poetic, and dialectal grammar forms",
        "expert discourse management in spoken and written language",
        "fine distinctions in formality and register in varied genres",
        "stylistic mastery of grammar for literary and academic texts"
    )

    fun turkishCourseA1Content() = listOf(
        "turkish alphabet and pronunciation",
        "personal pronouns",
        "possessive suffixes (-im, -in, -i, ...)",
        "definite and indefinite articles (concept explanation)",
        "noun cases: nominative and accusative",
        "plural suffix (-ler, -lar)",
        "present simple tense (aorist)",
        "negation with değil and -ma/-me",
        "forming yes/no questions with mı/mu/mü/mı",
        "question words (ne, kim, nerede, ne zaman, neden, nasıl)",
        "basic verb 'to be' (olmak) in present simple (implied)",
        "possessive constructions with suffixes",
        "personal and demonstrative pronouns (bu, şu, o)",
        "basic postpositions and prepositions",
        "possessive adjectives",
        "simple conjunctions (ve, ama, çünkü)",
        "ordinal and cardinal numbers (1–100)",
        "telling time and dates",
        "basic imperative mood",
        "past simple tense (di’li geçmiş zaman)",
        "basic greetings and introductions"
    )

    fun turkishCourseA2Content() = listOf(
        "noun cases: dative, locative, ablative, genitive",
        "possessive constructions with genitive + noun",
        "present continuous tense (-iyor)",
        "modal verbs (istemek, bilmek, gerek) in present tense",
        "future tense (-ecek, -acak)",
        "past continuous tense (-iyordu)",
        "comparatives and superlatives",
        "reflexive pronouns and reflexive verbs",
        "direct and indirect object suffixes",
        "negation with yok and other particles",
        "expressing ability and permission (bilmek, -ebilmek)",
        "conditional mood (eğer/ise)",
        "question forms with intonation and question words",
        "relative clauses with -en and -ki",
        "indirect speech basics",
        "conjunctions expressing cause and result",
        "expressing possession with var and yok",
        "expressions of necessity and obligation"
    )

    fun turkishCourseB1Content() = listOf(
        "past perfect tense (miş’li geçmiş zaman)",
        "pluperfect tense",
        "future perfect tense",
        "reported speech and indirect questions",
        "passive voice in present and past tenses",
        "causative constructions",
        "conditional sentences type 2 and 3",
        "use of participles as adjectives",
        "advanced relative clauses",
        "modal verbs in past and conditional tenses",
        "expressing wishes and hypotheticals",
        "complex sentence conjunctions (ancak, fakat, lakin)",
        "object pronoun suffix combinations",
        "expressing cause, purpose, concession",
        "expressions of time and aspect nuances"
    )

    fun turkishCourseB2Content() = listOf(
        "advanced use of subjunctive and optative moods (e.g., dilek kipi)",
        "complex passive constructions",
        "embedded clauses and noun clauses",
        "advanced conjunctions and discourse markers",
        "nuances of modality and politeness in verb endings",
        "expressions of contrast and concession",
        "conditional sentences with mixed tenses and moods",
        "advanced use of verbal nouns and adjectives",
        "ellipsis and topic-comment structures",
        "complex negation forms",
        "expressions of probability and doubt",
        "stylistic uses of verb forms in formal and literary contexts"
    )

    fun turkishCourseC1Content() = listOf(
        "stylistic inversion and emphasis in sentence structure",
        "advanced discourse cohesion and text organization",
        "rhetorical questions and indirect speech nuances",
        "use of archaic and literary verb forms",
        "expressions of irony, sarcasm, and humor grammatically",
        "complex nominalizations and abstract forms",
        "register variation and formality distinctions",
        "advanced pragmatic particles and modal expressions",
        "complex syntactic embedding and multiple subordinate clauses",
        "advanced conjunction and connective use for nuanced argumentation"
    )

    fun turkishCourseC2Content() = listOf(
        "mastery of all moods and verb forms with subtle distinctions",
        "integration of dialectal and archaic grammar forms",
        "expert manipulation of sentence structure for rhetorical effect",
        "precise use of modality, evidentiality, and politeness markers",
        "handling complex topic chains and ellipsis",
        "advanced discourse management in spoken and written contexts",
        "stylistic mastery of grammar for academic and literary texts",
        "fine distinctions in register, formality, and pragmatic nuance"
    )

    fun englishCourseA1Content() = listOf(
        "english alphabet and pronunciation basics",
        "personal pronouns (I, you, he, she, it, we, they)",
        "to be verb (am, is, are) in present simple",
        "subject-verb agreement in present simple",
        "simple present tense (regular verbs)",
        "simple negation with not",
        "forming yes/no questions with do/does",
        "question words (who, what, where, when, why, how)",
        "possessive adjectives (my, your, his, her, its, our, their)",
        "possessive ’s",
        "simple demonstratives (this, that, these, those)",
        "basic prepositions of place and time (in, on, at)",
        "basic conjunctions (and, but, or, because)",
        "countable and uncountable nouns basics",
        "plural nouns regular forms",
        "imperatives (commands)",
        "there is / there are",
        "simple adjectives and their order",
        "telling time and dates",
        "basic modal verbs (can for ability and permission)",
        "simple past tense of regular verbs (walked, played)"
    )

    fun englishCourseA2Content() = listOf(
        "simple past tense irregular verbs",
        "present continuous tense (am/is/are + verb-ing)",
        "questions in present continuous",
        "past continuous tense (was/were + verb-ing)",
        "future with going to and will",
        "modals for requests and offers (can, could, will, would)",
        "comparative and superlative adjectives",
        "adverbs of frequency and manner",
        "prepositions of movement and direction (to, into, out of)",
        "object pronouns (me, you, him, her, it, us, them)",
        "reflexive pronouns (myself, yourself, himself, etc.)",
        "countable vs uncountable nouns and quantifiers (some, any, much, many)",
        "present perfect simple (have/has + past participle) introduction",
        "expressing ability, obligation, and advice (can, must, should)",
        "phrases for making suggestions and offers"
    )

    fun englishCourseB1Content() = listOf(
        "past perfect tense (had + past participle)",
        "present perfect continuous tense",
        "future continuous and future perfect tenses",
        "reported speech basics (statements and questions)",
        "conditionals type 0 and 1",
        "relative clauses with who, which, that",
        "modal verbs for possibility, deduction, and advice (might, may, must, should)",
        "passive voice in present and past simple",
        "question tags",
        "expressing preferences and habits with would",
        "gerunds and infinitives basics",
        "phrasal verbs common types",
        "expressing wishes and regrets with would and if only",
        "indirect questions",
        "used to for past habits",
        "expressions of cause and effect (because, so, as a result)"
    )

    fun englishCourseB2Content() = listOf(
        "all conditionals including type 2 and 3",
        "mixed conditionals",
        "advanced reported speech (commands, requests, and questions)",
        "passive voice in all tenses and modals",
        "relative clauses with prepositions and reduced relative clauses",
        "modal perfect (should have, might have, could have)",
        "expressing contrast and concession (although, despite, whereas)",
        "advanced phrasal verbs and idiomatic expressions",
        "inversion for emphasis and in conditionals",
        "nominalisation and formal writing grammar",
        "expressions of degree and intensity",
        "collocations and fixed expressions",
        "complex sentence linking with connectors (however, therefore, nevertheless)",
        "ellipsis and substitution in discourse",
        "expressing purpose, result, and concession in complex sentences"
    )

    fun englishCourseC1Content() = listOf(
        "advanced modal verbs for nuanced meaning",
        "subjunctive mood in formal contexts",
        "advanced use of inversion and fronting",
        "complex noun phrases and embedded clauses",
        "ellipsis and anaphora in extended discourse",
        "discourse markers for cohesion and coherence",
        "expressions of formality and informality",
        "advanced connectors for argumentation and persuasion",
        "nominalisation in academic and professional writing",
        "advanced reported speech and narrative techniques",
        "nuances of tense and aspect in storytelling",
        "stylistic variation between spoken and written English",
        "register and tone adjustments for audience"
    )

    fun englishCourseC2Content() = listOf(
        "mastery of all verb forms and moods with subtle distinctions",
        "rhetorical devices in grammar and syntax",
        "complex sentence embedding and multiple subordinate clauses",
        "precision in modality, evidentiality, and politeness strategies",
        "handling of ellipsis, parallelism, and rhetorical repetition",
        "advanced discourse structuring and thematic progression",
        "genre-specific grammatical mastery (academic, literary, legal)",
        "integration of sociolinguistic and pragmatic competence",
        "stylistic mastery for creative and formal texts",
        "expertise in ambiguity and disambiguation in grammar"
    )

    fun arabicCourseA1Content() = listOf(
        "arabic alphabet and pronunciation",
        "short and long vowels",
        "definite article الـ (al-)",
        "personal pronouns",
        "noun gender (masculine and feminine)",
        "singular and plural forms (sound masculine and feminine plurals)",
        "nominal sentence basics (jumla ismiyya)",
        "verbal sentence basics (jumla fi‘liyya)",
        "present tense of regular verbs (المضارع)",
        "past tense of regular verbs (الماضي)",
        "negation particles (لا, ما)",
        "question words (من, ماذا, أين, متى, كيف, لماذا)",
        "basic prepositions (في, على, من, إلى)",
        "possessive constructions (إضافة - annexation)",
        "demonstrative pronouns (هذا, هذه, هؤلاء)",
        "imperative verbs (الأمر)",
        "basic conjunctions (و, لكن, أو, لأن)",
        "numbers 1-10",
        "basic greetings and simple sentences"
    )

    fun arabicCourseA2Content() = listOf(
        "dual form and broken plurals basics",
        "pronouns suffixes (attached pronouns)",
        "negation particles extended (لم, لن, ما زال)",
        "future tense particles (سـ, سوف)",
        "imperative negative (لا + present verb)",
        "comparative and superlative adjectives",
        "relative pronouns (الذي, التي, الذين)",
        "conditional sentences (إذا)",
        "verb forms I to X overview (المجموعة الصرفية)",
        "modal particles expressing necessity and possibility (يجب أن, يمكن أن)",
        "expressing possession with عند",
        "interrogative particles with attached pronouns",
        "verb moods: indicative, subjunctive, jussive (مرفوع, منصوب, مجزوم) basics",
        "expressing time and place with prepositions and adverbs",
        "basic negation in past and present"
    )

    fun arabicCourseB1Content() = listOf(
        "verb forms II to X detailed study",
        "past perfect and pluperfect tenses",
        "passive voice in past and present",
        "conditional sentences types 1 and 2",
        "subjunctive and jussive moods advanced use",
        "relative clauses with pronouns and omission",
        "indirect speech (المخاطبة الغير مباشرة)",
        "nominal sentence complex structures",
        "verbal nouns (مصدر) and participles (اسم الفاعل واسم المفعول)",
        "emphatic and focus particles",
        "modal verbs in past and present",
        "expressing obligation and prohibition (لازم, يجب)",
        "causative verb forms",
        "conjunctions expressing cause, purpose, concession",
        "expressing time with temporal clauses"
    )

    fun arabicCourseB2Content() = listOf(
        "advanced verb morphology and irregular verbs",
        "complex conditional sentences with mixed moods",
        "advanced subjunctive and jussive uses",
        "passive voice in all verb forms and tenses",
        "advanced relative clauses with prepositions",
        "complex sentence connectors for cause, concession, and contrast",
        "expressions of modality with nuanced shades (probability, doubt)",
        "expressions of emphasis and focus particles",
        "advanced negation forms and double negation",
        "nominalization and abstract noun formation",
        "complex discourse markers and cohesion devices",
        "stylistic uses of verb forms and particles in literature and formal speech"
    )

    fun arabicCourseC1Content() = listOf(
        "stylistic and rhetorical devices in grammar",
        "ellipsis and anaphora in complex texts",
        "use of archaic and classical forms in modern and literary Arabic",
        "advanced subordinate clauses embedding",
        "discourse topic management and thematic progression",
        "expressions of irony, emphasis, and modality in literary style",
        "advanced nominal and verbal sentence variation",
        "pragmatic use of particles and mood changes",
        "complex sentence inversion and emphasis",
        "register and dialectal variation in grammar"
    )

    fun arabicCourseC2Content() = listOf(
        "mastery of all verb forms with subtle morphological and syntactic distinctions",
        "expert manipulation of sentence structure for rhetorical and poetic effects",
        "handling multiple embeddings and syntactic complexity",
        "precision in modality, evidentiality, and politeness markers",
        "advanced discourse cohesion and coherence techniques",
        "integration of classical, modern standard, and dialectal grammar nuances",
        "stylistic mastery in literary, religious, and academic texts",
        "expert use of ambiguity and disambiguation in grammar",
        "genre-specific grammar mastery (legal, scientific, poetic)"
    )

    fun latinCourseA1Content() = listOf(
        "latin alphabet and pronunciation",
        "first declension nouns",
        "second declension nouns",
        "present tense active indicative (1st–4th conjugation)",
        "subject pronouns (implicit and for emphasis)",
        "verb-subject agreement",
        "basic negation (non)",
        "first and second declension adjectives",
        "noun-adjective agreement in gender, case, and number",
        "basic prepositions with accusative and ablative (in, ad, cum, sine, etc.)",
        "simple question words (quis, quid, ubi, cur, quando)",
        "simple yes/no questions with -ne",
        "imperative mood: present active",
        "present forms of sum and esse",
        "cardinal numbers 1–100",
        "days, months, telling time in classical style",
        "basic conjunctions (et, sed, aut, nam)"
    )

    fun latinCourseA2Content() = listOf(
        "third declension nouns (consonant and i-stem)",
        "fourth and fifth declension nouns",
        "irregular noun forms",
        "imperfect and future tense indicative active",
        "perfect, pluperfect, and future perfect indicative active",
        "perfect system of sum (fui, fueram, etc.)",
        "passive voice in present, imperfect, and future",
        "demonstrative pronouns (hic, ille, iste)",
        "personal pronouns (ego, tu, nos, vos)",
        "possessive adjectives (meus, tuus, suus)",
        "reflexive pronouns and adjectives",
        "comparative and superlative adjectives",
        "adverbs and their comparison",
        "prepositions with both cases (in, sub, etc.)",
        "basic uses of infinitives",
        "basic participles (present active, perfect passive)",
        "ablative of means, manner, accompaniment"
    )

    fun latinCourseB1Content() = listOf(
        "relative pronouns (qui, quae, quod)",
        "relative clauses with antecedents",
        "ablative absolute construction",
        "accusative + infinitive indirect speech",
        "subjunctive mood: present and imperfect",
        "uses of subjunctive in purpose and result clauses",
        "causal and concessive clauses with subjunctive",
        "indirect questions with subjunctive",
        "passive voice in all six tenses",
        "future passive participle (gerundive) and passive periphrastic",
        "participial phrases (ablative absolute, etc.)",
        "gerund and gerundive constructions",
        "conditional sentences (simple, future more vivid)",
        "uses of dative: purpose, reference",
        "advanced prepositional phrases and idioms"
    )

    fun latinCourseB2Content() = listOf(
        "subjunctive mood: pluperfect and perfect",
        "conditional sentences (future less vivid, contrary to fact)",
        "sequence of tenses in subjunctive clauses",
        "indirect statements and questions with multiple verbs",
        "more complex passive periphrastics and impersonal verbs",
        "impersonal constructions (licet, oportet, decet)",
        "expressions of necessity and obligation",
        "advanced gerundive uses",
        "substantive clauses (ut/ne + subjunctive)",
        "advanced uses of participles (temporal, causal)",
        "expressing purpose, cause, result with multiple structures",
        "correlative constructions (tam...quam, tanto...quanto)",
        "indirect commands (ut/ne + subjunctive)",
        "expressing emotion with accusative and infinitive",
        "expressing time with ablative and accusative constructions"
    )

    fun latinCourseC1Content() = listOf(
        "advanced use of all subjunctive moods",
        "narrative and literary uses of historical infinitive",
        "oratio obliqua in extended texts",
        "complex conditional and concessive periods",
        "rhetorical questions and deliberative subjunctive",
        "elliptical constructions and implied verbs",
        "poetic word order and stylistic inversion (hyperbaton)",
        "rhetorical devices involving grammar (chiasmus, asyndeton)",
        "advanced uses of particles (enim, vero, autem)",
        "figurative and idiomatic constructions",
        "register and stylistic variation (Cicero vs Caesar vs Ovid)",
        "grammatical cohesion in extended discourse"
    )

    fun latinCourseC2Content() = listOf(
        "mastery of all verb tenses, voices, moods",
        "comprehensive use of subordinate clauses in embedded structures",
        "free indirect discourse and syntactic variation",
        "complex poetic constructions and meter-influenced syntax",
        "mastery of stylistic variations and high-register grammar",
        "manipulation of word order for emphasis and rhythm",
        "integration of rare and archaic forms",
        "nuanced expressions of modality, doubt, emotion, obligation",
        "comprehensive command of classical idiom and expression",
        "deep textual analysis through grammatical structure"
    )

    fun esperantoCourseA1Content() = listOf(
        "alphabet and pronunciation",
        "nouns ending in -o",
        "adjectives ending in -a and agreement with nouns",
        "plural formation with -j",
        "accusative case with -n",
        "definite article 'la'",
        "personal pronouns (mi, vi, li, ŝi, ĝi, ni, ili)",
        "present tense of regular verbs (-as)",
        "negation with 'ne'",
        "yes/no questions with 'ĉu'",
        "basic question words (kiu, kio, kie, kiam, kial, kiel)",
        "basic prepositions (en, sur, sub, kun, sen, al)",
        "cardinal numbers (unu, du, tri...)",
        "basic conjunctions (kaj, sed, aŭ, ĉar)",
        "imperative mood (-u, singular commands)",
        "correlatives introduction (ki-, ti-, i-, ĉiu, neniu)",
        "possessive pronouns (mia, via, lia, ŝia...)"
    )

    fun esperantoCourseA2Content() = listOf(
        "past tense of verbs (-is)",
        "future tense of verbs (-os)",
        "infinitive (-i) and use after modal verbs",
        "imperative and volitive mood (-u)",
        "correlative system full overview (ki-, ti-, i-, ĉ-, nen-)",
        "reflexive pronoun 'si'",
        "adverbs ending in -e",
        "forming comparatives and superlatives (pli, plej)",
        "direct and indirect object usage with -n",
        "prepositions for time and direction (ĝis, ekde, dum)",
        "temporal expressions with accusative",
        "possessive constructions with 'de'",
        "forming compound words",
        "word order and sentence structure basics",
        "impersonal verbs (pluvas, ŝajnas)"
    )

    fun esperantoCourseB1Content() = listOf(
        "conditional mood (-us)",
        "participles: active and passive (anta, inta, onta, ata, ita, ota)",
        "use of participles in compound tenses",
        "passive voice constructions (esti + participle)",
        "expressing obligation and necessity (devas, bezonas, necesas)",
        "use of 'igi' and 'iĝi' for causative and inchoative",
        "prefixes and suffixes (mal-, -et-, -eg-, -em-, -ul-, -in-, -ej-, -il-)",
        "derivation of new words via affixes",
        "advanced correlatives in subordinate clauses",
        "relative pronouns and clauses",
        "use of 'oni' (impersonal subject)",
        "indirect speech",
        "conditional sentences with -us",
        "negation in complex sentences",
        "usage of prepositions with abstract nouns"
    )

    fun esperantoCourseB2Content() = listOf(
        "stylistic variation in word order for emphasis",
        "multiple subordinate clauses in a sentence",
        "expressing concession, contrast, and cause (kvankam, tamen, ĉar)",
        "expressing purpose and result (por ke, tiel ke)",
        "subtle distinctions with participial phrases",
        "discourse markers (do, nu, ja, eble)",
        "rhetorical questions and emphasis with 'ĉu ne', 'ĉu do'",
        "abstract noun formation (-eco, -ado, -aĵo)",
        "modality with adverbs (eble, certe, verŝajne)",
        "advanced passive constructions with aspect nuance",
        "reduplication and poetic inversion",
        "register differences: formal vs informal tone",
        "metaphorical extensions of basic verbs"
    )

    fun esperantoCourseC1Content() = listOf(
        "advanced use of participial chains in narration",
        "rhetorical and poetic use of correlatives and affixes",
        "ellipsis and grammatical economy in speech and writing",
        "stylistic inversion for rhythm and clarity",
        "advanced impersonal and passive constructions",
        "dialectal and stylistic variation in usage",
        "nuanced aspect expression using word choice and prefixation",
        "complex argument structure with prepositional verbs",
        "sociolinguistic variation and register adaptation",
        "grammatical cohesion in academic or literary texts"
    )

    fun esperantoCourseC2Content() = listOf(
        "mastery of all affixal derivation strategies",
        "handling idiomatic and figurative expressions grammatically",
        "integration of multiple grammatical devices for style",
        "syntactic manipulation for irony, contrast, and focus",
        "discourse-level cohesion in persuasive and expository writing",
        "full integration of correlative system in high-level discourse",
        "precise control of verb aspect through compound forms",
        "stylistic grammar in poetry and literary translation",
        "grammatical strategies for ambiguity and subtle emphasis",
        "dialectal, historical, and stylistic grammar variants"
    )

    fun polishCourseA1Content() = listOf(
        "польский алфавит и произношение",
        "личные местоимения (ja, ty, on, ona, my, wy, oni, one)",
        "настоящее время глаголов I спряжения",
        "глагол być в настоящем времени",
        "глагол mieć в настоящем времени",
        "существительные: род (мужской, женский, средний)",
        "множественное число существительных",
        "именительный падеж (Nominativus)",
        "винительный падеж (Accusativus)",
        "прилагательные и согласование с существительными",
        "числительные от 1 до 100",
        "вопросительные слова (kto, co, gdzie, kiedy, dlaczego, jak)",
        "простое отрицание с nie",
        "порядок слов в утвердительном предложении",
        "простые предлоги (w, na, do, z, przy, pod)",
        "основные союзы (i, ale, bo, lub)",
        "дни недели, месяцы, время суток",
        "императив 2-го лица (простые команды)"
    )

    fun polishCourseA2Content() = listOf(
        "прошедшее время глаголов (czas przeszły)",
        "будущее время: сложная форма (быть + инфинитив)",
        "будущее время: простая форма совершенных глаголов",
        "вид глагола: совершенный и несовершенный",
        "одушевлённость в винительном падеже",
        "родительный падеж (Genitivus) после отрицания и количеств",
        "местный падеж (Locativus) с предлогами",
        "творительный падеж (Instrumentalis) в утвердительных предложениях",
        "дательный падеж (Dativus) для косвенных дополнений",
        "возвратные глаголы с się",
        "притяжательные местоимения (mój, twój, jego, jej...)",
        "сравнительная и превосходная степень прилагательных",
        "сложные числительные и счётные конструкции",
        "предлоги направления и времени (do, od, przez, po)",
        "условные предложения с jeśli и gdyby (простые формы)",
        "глаголы движения (iść, jechać, chodzić, jeździć)",
        "неопределённые местоимения (ktoś, coś, nikt, nic)"
    )

    fun polishCourseB1Content() = listOf(
        "условное наклонение (tryb przypuszczający)",
        "страдательный залог (forma bierna)",
        "глагольные конструкции с инфинитивом",
        "сложноподчинённые предложения (że, żeby, chociaż, ponieważ)",
        "косвенная речь (mowa zależna)",
        "порядок слов в сложных предложениях",
        "употребление видо-временных форм в повествовании",
        "сложные формы прошедшего времени (czas zaprzeszły)",
        "деепричастия (имперфектива и перфектива)",
        "эмфатические частицы (nawet, właśnie, dopiero, już)",
        "предлоги с разными падежами",
        "вежливые формы обращения (Pan, Pani + 3 лицо)",
        "степени сравнения наречий",
        "причинно-следственные союзы (bo, ponieważ, dlatego że)"
    )

    fun polishCourseB2Content() = listOf(
        "конструкции с причастиями и деепричастиями",
        "употребление пассива в разных временах",
        "инверсия и акцент в структуре предложения",
        "порядок слов в тексте с логическим акцентом",
        "глаголы с управлением (rekcja czasownika)",
        "выражения цели, причины и следствия в сложных предложениях",
        "различие значений при смене вида глагола",
        "стилистические различия между формами прошедшего времени",
        "переходные и непереходные глаголы",
        "метафорическое употребление глаголов и предлогов",
        "согласование времён в косвенной речи",
        "модальные конструкции (można, trzeba, należy, da się)",
        "условные и уступительные конструкции (nawet jeśli, mimo że)"
    )

    fun polishCourseC1Content() = listOf(
        "речь высокого стиля и литературные конструкции",
        "оттенки значений видо-временных форм",
        "градация прилагательных и наречий в контексте",
        "сложные условные конструкции с gdyby, jakby, chociażby",
        "риторические конструкции и инверсия",
        "конструкции с количественными выражениями (tyle, aż tyle, zbyt)",
        "метафорические и образные конструкции в грамматике",
        "переход от прямой к косвенной речи в длинных отрывках",
        "конструкции с 'żeby' и 'aby' в роли цели и пожелания",
        "грамматическая связность и структура академического текста"
    )

    fun polishCourseC2Content() = listOf(
        "полное владение аспектом и временем глагола в любом контексте",
        "стилистическая трансформация грамматических структур",
        "использование эллипсиса, инверсии и эмфазы",
        "риторические вопросы и грамматические фигуры",
        "регистровая адаптация (разговорный, нейтральный, официальный)",
        "различия в грамматике письменной и устной речи",
        "вариативность и допустимые отклонения от нормы",
        "высокоуровневая связность в письменной и устной речи",
        "грамматическая интерпретация художественного текста",
        "диалектные, исторические и региональные грамматические формы"
    )

    fun serbianCourseA1Content() = listOf(
        "сербский алфавит: кириллица и латиница",
        "произношение и ударение",
        "личные местоимения (ja, ti, on, ona, ono, mi, vi, oni, one)",
        "настоящее время глаголов I группы (-ати)",
        "глаголы 'biti' и 'imati' в настоящем времени",
        "существительные: род (мужской, женский, средний)",
        "именительный падеж (nominativ) в ед. и мн. числе",
        "винительный падеж (akuzativ) с одушевлёнными и неодушевлёнными",
        "прилагательные и согласование с существительными",
        "порядок слов в простом предложении",
        "отрицание с 'не'",
        "вопросительные слова (ко, шта, где, кад, како, зашто)",
        "члены семьи, дни недели, числительные 1–100",
        "простые предлоги (у, на, са, без, до, из)",
        "основные союзы (и, али, јер, или)",
        "повелительное наклонение: ед. число",
        "вежливая форма обращения (ви + глагол в 2 лице мн.ч.)"
    )

    fun serbianCourseA2Content() = listOf(
        "прошедшее время (перфекат) правильных глаголов",
        "будущее время I (с глаголом 'ћу')",
        "родительный падеж (genitiv): формы и основные значения",
        "дательный падеж (dativ): направления, адресаты",
        "творительный падеж (instrumental): средство и сопровождение",
        "местный падеж (lokativ): предлоги места и времени",
        "возвратные глаголы с 'се'",
        "склонение притяжательных местоимений",
        "вид глагола: несовершенный и совершенный",
        "сравнительная и превосходная степень прилагательных",
        "составные числительные и счётные конструкции",
        "повелительное наклонение: множественное число",
        "условные предложения с 'ако'",
        "придаточные предложения с 'да', 'јер', 'када'",
        "модальные выражения: могу, морам, желим"
    )

    fun serbianCourseB1Content() = listOf(
        "будущее время II (перфекат + да)",
        "имперфекат: обзор и употребление",
        "плюсквамперфекат (pluskvamperfekat): образование и использование",
        "страдательный залог с 'бити' + причастие",
        "косвенная речь (индиректни говор)",
        "относительные местоимения и предложения (који, чији...)",
        "наречия: степени сравнения и образование",
        "модальные глаголы в прошедшем времени",
        "предлоги в переносных значениях",
        "глаголы движения и направление (ићи, долазити, улазити...)",
        "подчинённые предложения цели, причины, времени",
        "условные предложения с 'да', 'ако', 'када'",
        "согласование времён в косвенной речи",
        "конструкции с инфинитивом и 'да'-формой",
        "употребление частицы 'се' в возвратных и страдательных значениях"
    )

    fun serbianCourseB2Content() = listOf(
        "причастия и деепричастия: формы и функции",
        "страдательный залог в разных временах",
        "инверсия и эмфаза в предложении",
        "словопорядок в сложных конструкциях",
        "глагольные конструкции с управлением (радовати се нечему)",
        "употребление модальных выражений и перифразов",
        "различие видо-временных форм в рассказе",
        "стилистические оттенки падежей",
        "причинно-следственные, уступительные и целевые союзы",
        "вежливая и официальная речь",
        "формы и употребление числительных в сложных падежах",
        "регистровые и разговорные конструкции"
    )

    fun serbianCourseC1Content() = listOf(
        "речевая стилистика: литературный vs разговорный язык",
        "риторические конструкции и усиление",
        "грамматические фигуры речи (повтор, противопоставление)",
        "инверсии и эллипсис в публицистике и литературе",
        "вложенные придаточные предложения",
        "высокий уровень согласования времён",
        "стилистическая вариативность прилагательных и наречий",
        "переносные значения предлогов и глаголов",
        "глагольные конструкции в юридическом и научном стиле",
        "логическая связность текста средствами грамматики"
    )

    fun serbianCourseC2Content() = listOf(
        "полное владение грамматикой в любом регистре",
        "грамматические особенности поэтической и архаичной речи",
        "структуры для выразительности: инверсия, парцелляция, эллипсис",
        "грамматическая точность в академической и публичной речи",
        "вариативность и отклонения от нормы в авторской речи",
        "употребление редких и книжных форм",
        "стилистическая трансформация высказываний",
        "социолингвистические и диалектные особенности грамматики",
        "анализ и создание текстов с высокой грамматической сложностью",
        "грамматическое оформление абстрактных понятий и категорий"
    )

    fun greekCourseA1Content() = listOf(
        "греческий алфавит и произношение",
        "личные местоимения (εγώ, εσύ, αυτός...)",
        "настоящее время правильных глаголов",
        "глаголы είμαι и έχω в настоящем времени",
        "определённый артикль (ο, η, το)",
        "неопределённый артикль (ένας, μία, ένα)",
        "род существительных (мужской, женский, средний)",
        "падежи: именительный и винительный (ενικός αριθμός)",
        "множественное число существительных и артиклей",
        "прилагательные и согласование по роду, числу и падежу",
        "порядок слов в утвердительном предложении",
        "отрицание с δεν и δεν είναι",
        "вопросы с интонацией и с вопросительным словом",
        "вопросительные слова (τι, ποιος, πού, πότε, γιατί, πώς)",
        "основные числительные (1–100)",
        "дни недели, месяцы, время суток",
        "простые предлоги (σε, από, με, για)",
        "простые союзы (και, αλλά, γιατί, ή)",
        "повелительное наклонение (2-е лицо ед. ч.)"
    )

    fun greekCourseA2Content() = listOf(
        "прошедшее время: аорист (αόριστος)",
        "прошедшее время: имперфект (παρατατικός)",
        "будущее время (θα + глагол)",
        "возвратные глаголы (π.χ. ξυπνιέμαι, ντύνομαι)",
        "местоимения в винительном падеже (με, σε, τον, τη...)",
        "родительный падеж (γενική) существительных и местоимений",
        "предлоги направления и времени (προς, μέχρι, από, πριν)",
        "отрицание с μην в повелительном наклонении",
        "повелительное наклонение (2-е лицо мн. ч.)",
        "сравнительная и превосходная степень прилагательных",
        "числительные: порядковые (πρώτος, δεύτερος...)",
        "предложения с γιατί και επειδή (причина)",
        "условные предложения с αν и όταν (простые формы)",
        "согласование времён в простых предложениях",
        "глаголы движения και καθημερινές ενέργειες"
    )

    fun greekCourseB1Content() = listOf(
        "причастия и деепричастные обороты (μετοχές)",
        "сложные времена: παρακείμενος и υπερσυντέλικος",
        "будущее совершенное (μέλλοντας συντελεσμένος)",
        "сослагательное наклонение (υποτακτική): образование и употребление",
        "условные предложения: тип 1 и тип 2",
        "косвенная речь (πλάγιος λόγος)",
        "пассивный залог во всех временах",
        "местоимения в дательном падеже (μου, σου, του...)",
        "модальные выражения (πρέπει να, μπορεί να, θέλω να)",
        "речевые обороты с να και πως",
        "формирование сложных предложений",
        "вежливая форма обращения (3 лицо ед. и мн. числа)",
        "степени сравнения наречий и прилагательных",
        "образование наречий и наречные конструкции"
    )

    fun greekCourseB2Content() = listOf(
        "придаточные предложения: времени, причины, цели, уступки",
        "усложнённые условные конструкции (если бы...)",
        "согласование времен в сложноподчинённых предложениях",
        "глаголы с особым управлением (φοβάμαι, χρειάζομαι κ.λπ.)",
        "сложные глагольные формы в повествовании и описании",
        "выражения мнения, сомнения, желания с υποτακτική",
        "пассивные конструкции с από и через неопределённые агенты",
        "лексико-грамматические конструкции с предлогами",
        "грамматические особенности разговорной и письменной речи",
        "метафорическое употребление глаголов и предлогов",
        "структуры согласования подлежащего и дополнения"
    )

    fun greekCourseC1Content() = listOf(
        "стилистическое использование подчинительных союзов",
        "различие между формальным и разговорным стилем",
        "эмфаза, инверсия и порядок слов",
        "конструкции с безличными выражениями (είναι απαραίτητο να...)",
        "длинные цепочки согласованных времен и наклонений",
        "выражения в академической и публицистической речи",
        "риторические и экспрессивные конструкции",
        "фразеологизмы и грамматические идиомы",
        "употребление артикля в абстрактных выражениях",
        "предложения с сложной логической структурой (αν και, παρόλο που)"
    )

    fun greekCourseC2Content() = listOf(
        "управление регистрами: литературный, научный, разговорный",
        "полный контроль над согласованием времен и наклонений",
        "риторические фигуры речи и синтаксическая трансформация",
        "эллипсис, инверсия, парцелляция в тексте",
        "интерпретация сложных грамматических структур в текстах",
        "выражение оценочности, сомнения, иронии грамматически",
        "лексико-грамматическая вариативность и синонимичные структуры",
        "употребление архаизмов и диалектизмов в художественной речи",
        "анализ поэтической и прозаической грамматики",
        "грамматическая стилистика в аргументативных и эссеистических жанрах"
    )


}
