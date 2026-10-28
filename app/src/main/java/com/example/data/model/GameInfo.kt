package com.example.data.model

data class GameInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val badge: String,
    val description: String,
    val durationText: String,
    val accentColor: Long,
    val rules: List<String>
)

object GameCatalog {
    val games = listOf(
        GameInfo(
            id = "morpion",
            title = "Morpion",
            subtitle = "Tic-Tac-Toe Néon",
            iconEmoji = "❌⭕",
            badge = "Classique",
            description = "Alignez 3 symboles identiques sur la grille 3x3 pour remporter la victoire.",
            durationText = "1-2 min",
            accentColor = 0xFFA855F7,
            rules = listOf(
                "Grille 3x3 tour par tour",
                "Le premier joueur est X, le second O",
                "Alignez 3 symboles horizontalement, verticalement ou en diagonale",
                "Égalité si la grille est remplie sans alignement"
            )
        ),
        GameInfo(
            id = "chifoumi",
            title = "Pierre, Papier, Ciseaux",
            subtitle = "Manche en 3 points",
            iconEmoji = "✊✋✌️",
            badge = "Rapide",
            description = "Duels psychologiques intenses : vos choix sont cachés jusqu'à validation simultanée !",
            durationText = "1 min",
            accentColor = 0xFF06B6D4,
            rules = listOf(
                "Manche en 3 points gagnants",
                "Choix secret jusqu'à validation des 2 joueurs",
                "Pierre bat Ciseaux, Papier bat Pierre, Ciseaux bat Papier",
                "Révélation instantanée et attribution des points"
            )
        ),
        GameInfo(
            id = "quiz",
            title = "Quiz Battle",
            subtitle = "Culture & Réflexes",
            iconEmoji = "⚡🧠",
            badge = "Trivia",
            description = "10 questions de culture générale. Plus vous répondez vite et juste, plus vous marquez de points !",
            durationText = "2-3 min",
            accentColor = 0xFFEC4899,
            rules = listOf(
                "10 questions aléatoires avec 4 propositions",
                "15 secondes maximum par question",
                "Score basé sur l'exactitude et la vitesse",
                "Le joueur avec le score total le plus élevé gagne"
            )
        ),
        GameInfo(
            id = "puissance4",
            title = "Puissance 4",
            subtitle = "Alignement tactique",
            iconEmoji = "🔴🟡",
            badge = "Stratégie",
            description = "Faites tomber vos jetons dans la grille 7x6 pour former un alignement de 4 jetons.",
            durationText = "3-4 min",
            accentColor = 0xFFF59E0B,
            rules = listOf(
                "Grille verticale 7 colonnes x 6 rangées",
                "Les jetons tombent au bas de la colonne choisie",
                "Alignez 4 jetons de votre couleur",
                "Victoire horizontale, verticale ou diagonale"
            )
        ),
        GameInfo(
            id = "imposteur",
            title = "Imposteur",
            subtitle = "Mots secrets & Bluff",
            iconEmoji = "🕵️‍♂️💬",
            badge = "Bluff",
            description = "Deux mots secrets très proches. Donnez vos indices en 3 tours sans vous faire démasquer !",
            durationText = "3 min",
            accentColor = 0xFF10B981,
            rules = listOf(
                "Chaque joueur reçoit un mot secret proche (ex: Chat / Chien)",
                "3 tours : chacun donne un indice en 1 mot sur son mot",
                "Phase de vote : devinez si l'autre a un mot différent et lequel",
                "Points attribués pour la déduction et le camouflage"
            )
        ),
        GameInfo(
            id = "billard",
            title = "Billard 2D",
            subtitle = "Visée & Physique",
            iconEmoji = "🎱",
            badge = "Physique",
            description = "Version tour par tour 2D : glissez pour viser et doser la puissance. Premier à empocher gagne !",
            durationText = "4-5 min",
            accentColor = 0xFF6366F1,
            rules = listOf(
                "Table de billard 2D avec 7 billes par joueur",
                "Visée tactile intuitive et jauge de puissance",
                "Physique des rebonds et des collisions de billes",
                "Le premier à empocher toutes ses billes remporte la partie"
            )
        )
    )

    fun getGame(id: String): GameInfo {
        return games.find { it.id == id } ?: games.first()
    }
}
