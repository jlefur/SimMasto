\name{weed.fun}
\alias{weed.fun}
\title{Fonction de base du mod\`ele "Weed"}
\usage{weed.fun(decision, param)
}
\description{Fonction de base du modèle "Weed": calcul sur 1 année du modèle "Weed".}
\value{un vecteur de longueur 5 composé de:
\itemize{
\item{S}{production de graines par eqn(m^2)}
\item{d}{densité d'adventices à l'émergence (plantes par eqn(m^2))}
\item{SSBa}{banque de graines en surface après travail du sol (graines par eqn(m^2))}
\item{DSBa}{banque de graines en profondeur après travail du sol (graines par eqn(m^2))}
\item{Yield}{rendement (t par ha)}
}}
\arguments{\item{decision}{data.frame à 1 ligne et 3 colonnes Soil, Crop, Herb}
\item{param}{vecteur des paramètres:
mu, v, phi, beta.1, beta.0, chsi.1, chsi.0,
delta.new, delta.old, mh, mc, Smax.1, Smax.0,
Ymax, rmax, gamma
et des variables d'état initiales
d.im1, S.im1, SSBa.im1, DSBa.im1 (d,S,SSBa,DSBa)}
}

