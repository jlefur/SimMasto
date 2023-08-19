\name{weed.model}
\alias{weed.model}
\title{Mod\`ele "Weed" pour un jeu de param\`etres et un jeu de d\'ecisions}
\usage{weed.model(param, decision=weed.decision, tout=FALSE)
}
\description{Modèle "Weed" pour un jeu de paramètres et un jeu de décisions sur n années}
\note{Voir weed.factors pour les valeurs min, max et nominal des parametres}
\value{une matrice n x 5 composée de:
\itemize{
\item{S}{production de graines par eqn(m^2)}
\item{d}{densité d'adventices à l'émergence (plantes par eqn(m^2))}
\item{SSBa}{banque de graines en surface après travail du sol (graines par eqn(m^2))}
\item{DSBa}{banque de graines en profondeur après travail du sol (graines par eqn(m^2))}
\item{Yield}{rendement (t par ha)}
}}
\arguments{\item{param}{vecteur des paramètres:
mu, v, phi, beta.1, beta.0, chsi.1, chsi.0,
delta.new, delta.old, mh, mc, Smax.1, Smax.0,
Ymax, rmax, gamma
et des variables d'état initiales
d.im1, S.im1, SSBa.im1, DSBa.im1 (d,S,SSBa,DSBa)}
\item{decision}{data.frame à 3 colonnes Soil, Crop, Herb de valeurs 0-1
et n lignes, où n est le nombre d'années simulées}
\item{tout}{TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie}
}
\examples{decision <- data.frame(Soil=c(0,1),Crop=c(0,1),Herb=c(0,1))
weed.model( weed.factors$nominal, decision=decision )}

