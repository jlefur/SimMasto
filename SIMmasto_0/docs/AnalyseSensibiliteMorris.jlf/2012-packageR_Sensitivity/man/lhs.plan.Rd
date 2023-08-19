\name{lhs.plan}
\alias{lhs.plan}
\title{Tire selon le plan hyper-cube latin un \'echantillon de valeurs de param\`etres...}
\usage{lhs.plan(taille, plage, repet, tout=FALSE)
}
\description{Tire selon le plan hyper-cube latin un échantillon de valeurs de paramètres}
\value{la matrice \eqn{N \times p} de l'échantillon si \code{tout=FALSE}
ou une liste à 2 composantes, plan et lhs.tirage, si \code{tout=TRUE}}
\note{L'objet .factors donné dans l'argument plage sert à spécifier le nom
des facteurs et les bornes de leurs intervalles d'incertitude.
L'argument repet permet d'inclure un facteur supplémentaire qualitatif, dont les
modalités sont obtenues par des tirages aléatoires indépendants avec remise.}
\arguments{\item{taille}{de l'échantillon}
\item{plage}{objet de type .factors}
\item{repet}{NULL ou une ligne d'un objet de type .factors}
\item{tout}{TRUE si l'on veut conserver l'échantillon de base dans \eqn{[0,1]^p}}
}
\examples{lhs.plan( taille = 10, plage = fungus.factors, repet = NULL, tout = FALSE)
lhs.plan( taille = 10, plage = wwdm.factors[1:7,],
                       repet = wwdm.factors[8,], tout = FALSE)}

