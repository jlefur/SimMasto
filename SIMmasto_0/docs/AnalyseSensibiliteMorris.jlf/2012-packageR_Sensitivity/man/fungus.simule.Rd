\name{fungus.simule}
\alias{fungus.simule}
\title{Simulation du mod\`ele "fungus"...}
\usage{fungus.simule(X, temperature=10, tout=FALSE)
}
\description{Simulation du modèle "fungus"}
\note{Des valeurs min, max et nominales des paramètres sont donnees
dans fungus.factors. Elles correspondent à Alternaria
Brassicae, ravageur du colza (Magarey et al, 2005).}
\value{Data.frame a N lignes et p colonnes, ou p est la longueur de 'temperature'}
\references{Magarey RD, Sutton TB, Thayer CL (2005). A simple generic infection
model for foliar fungal plant pathogens. Phytopathology 95, 92-100.}
\arguments{\item{X}{vecteur de longueur 5 ou matrice N x 5 comprenant un
ou plusieurs jeux de valeurs de
Tmin, Topt, Tmax, Wmin, Wmax}
\item{temperature}{scalaire ou vecteur de temperatures}
\item{tout}{TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie}
}
\examples{scenarios <- rbind(fungus.factors$binf,fungus.factors$nominal,fungus.factors$bsup)
fungus.simule( scenarios, temperature=c(10,15,18,21,30) )}

