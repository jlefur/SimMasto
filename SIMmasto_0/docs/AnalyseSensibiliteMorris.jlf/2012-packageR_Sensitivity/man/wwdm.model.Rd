\name{wwdm.model}
\alias{wwdm.model}
\alias{wwdm.model}
\title{Mod\`ele "wwdm" pour un jeu de param\`etres}
\usage{wwdm.model(param, year, climate=wwdm.climates)
}
\description{Modèle wwdm (winter wheat dry matter) de croissance du blé,
modèle de culture très simple, dynamique à pas de temps journalier}
\note{Le modèle a deux variables d'état, l'indice de surface foliaire (LAI) et
la biomasse aérienne du blé d'hiver. La sortie de la fonction est le gain de
poids journalier de la matière sèche en 'g per m2 per day'.
Une simulation correspond à une année climatique. Par défaut, les données
climatiques sont lues dans le data.frame wwdm.climates, qui contient 14
années climatiques, et l'année doit être spécifiée par un nombre entre 1 et 14.
Il y a deux façons de faire cela, soit par l'argument \code{year}, soit
par la 8ème coordonnée de l'argument \code{param} si \code{year=NULL}. Lorsque
\code{year=NULL} et qu'il n'y a que 7 coordonnées dans \code{param}, l'année
utilisée est l'année numéro 3}
\value{vecteur des 223 gains journaliers de biomasse calculés par WWDM}
\references{Makowski, D., Jeuffroy, M.-H., Guérif, M., 2004 Bayesian methods for
updating crop model predictions, applications for predicting biomass and
grain protein content. In: Bayesian Statistics and Quality Modelling in
the Agro-Food Production Chain (van Boeakel et al. eds), pp. 57-68.
Kluwer, Dordrecht.

Monod, H., Naud, C., Makowski, D., 2006 Uncertainty and sensitivity
analysis for crop models. In: Working with Dynamic Crop Models (Wallach
D., Makowski D. and Jones J. eds), pp. 55-100. Elsevier, Amsterdam}
\arguments{\item{param}{vecteur de paramètres de wwdm de longueur 7 ou 8}
\item{year}{soit NULL soit un nombre compris entre 1 et 14}
\item{climate}{nom du data.frame contenant les données climatiques}
}
\examples{#data()
#wwdm.model()
#sum( wwdm.model() )  #biomasse cumulee
#wwdm.model(param=wwdm.factors$nominal, year=NULL, climate=wwdm.climates)
#wwdm.model(param=wwdm.factors$nominal, year=5)}

