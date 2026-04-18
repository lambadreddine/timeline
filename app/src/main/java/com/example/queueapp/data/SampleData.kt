package com.example.queueapp.data

// Constantine, Algeria  ~36.365°N, 6.614°E
object SampleData {

    val entities: List<Entity> = listOf(
        Entity(
            id = "entity_laposte",
            name = "La Poste",
            latitude = 36.3643,
            longitude = 6.6126,
            desks = listOf(
                Desk(
                    id = "desk_laposte_courrier",
                    name = "Courrier",
                    description = "Réception et envoi de lettres et colis",
                    currentServing = 0,
                    ticketCounter = 1
                ),
                Desk(
                    id = "desk_laposte_finances",
                    name = "Services Financiers",
                    description = "Mandats, virements et retraits",
                    currentServing = 0,
                    ticketCounter = 1
                ),
                Desk(
                    id = "desk_laposte_ccp",
                    name = "Compte CCP",
                    description = "Opérations sur compte courant postal",
                    currentServing = 0,
                    ticketCounter = 1
                )
            )
        ),
        Entity(
            id = "entity_mairie",
            name = "Mairie de Constantine",
            latitude = 36.3672,
            longitude = 6.6160,
            desks = listOf(
                Desk(
                    id = "desk_mairie_etatcivil",
                    name = "État Civil",
                    description = "Actes de naissance, mariage et décès",
                    currentServing = 0,
                    ticketCounter = 1
                ),
                Desk(
                    id = "desk_mairie_urbanisme",
                    name = "Urbanisme",
                    description = "Permis de construire et de démolir",
                    currentServing = 0,
                    ticketCounter = 1
                )
            )
        ),
        Entity(
            id = "entity_daira",
            name = "Daïra de Constantine",
            latitude = 36.3710,
            longitude = 6.6085,
            desks = listOf(
                Desk(
                    id = "desk_daira_cnrn",
                    name = "CNRN / CNI",
                    description = "Renouvellement carte nationale d'identité",
                    currentServing = 0,
                    ticketCounter = 1
                ),
                Desk(
                    id = "desk_daira_passport",
                    name = "Passeport",
                    description = "Demande et renouvellement de passeport",
                    currentServing = 0,
                    ticketCounter = 1
                ),
                Desk(
                    id = "desk_daira_permis",
                    name = "Permis de Conduire",
                    description = "Permis de conduire et duplicata",
                    currentServing = 0,
                    ticketCounter = 1
                )
            )
        ),
        Entity(
            id = "entity_cnas",
            name = "CNAS Constantine",
            latitude = 36.3580,
            longitude = 6.6210,
            desks = listOf(
                Desk(
                    id = "desk_cnas_sante",
                    name = "Remboursement Santé",
                    description = "Remboursements médicaux et pharmaceutiques",
                    currentServing = 0,
                    ticketCounter = 1
                ),
                Desk(
                    id = "desk_cnas_retraite",
                    name = "Retraite",
                    description = "Dossiers de retraite et pensions",
                    currentServing = 0,
                    ticketCounter = 1
                )
            )
        )
    )
}
