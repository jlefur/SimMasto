\name{weed.simule}
\alias{weed.simule}
\title{Simulations en s\'erie du mod\`ele "Weed"}
\usage{weed.simule(X, decision=weed.decision, sortie="rdt.total",
     nom.sortie, tout=FALSE)
}
\description{Simulations en série du modèle "Weed"}
\note{Le paramètre 'sortie' peut être:
\itemize{
\item soit une fonction calculant la ou les variables de sortie de chaque
simulation à partir du tableau n x 5 des sorties de 'weed.model';
\item soit un mot-cle pré-défini:
\itemize{
\item{annee.finale}{pour avoir les 5 sorties de la dernière année;}
\item{rdt.total}{pour avoir la somme des rendements sur les n annees (defaut);}
\item{banque.finale}{pour avoir la banque de graines en derniere annee.}
}}
Pour rappel, les 5 sorties de 'weed.model' sont:
\itemize{
\item{S}{production de graines par eqn(m^2)}
\item{d}{densité d'adventices à l'émergence (plantes par eqn(m^2))}
\item{SSBa}{banque de graines en surface après travail du sol (graines par eqn(m^2))}
\item{DSBa}{banque de graines en profondeur après travail du sol (graines par eqn(m^2))}
\item{Yield}{rendement (t par ha)}
}}
\value{un data.frame incluant en colonnes la ou les sorties retenues. Suivant la
valeur de 'tout', les entrées sont restituées ou non.}
\arguments{\item{X}{matrice ou data.frame des jeux de paramètres:
mu, v, phi, beta.1, beta.0, chsi.1, chsi.0,
delta.new, delta.old, mh, mc, Smax.1, Smax.0,
Ymax, rmax, gamma
et des variables d'état initiales
d.im1, S.im1, SSBa.im1, DSBa.im1 (d,S,SSBa,DSBa)}
\item{decision}{data.frame à 3 colonnes Soil, Crop, Herb et n
lignes, où n est le nombre d'années simulées}
\item{sortie}{fonction ou mot-clé donnant la nature de la ou des variables
en sortie de chaque simulation (voir DETAILS)}
\item{nom.sortie}{noms de la ou des variables de sortie retenues}
\item{tout}{TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie}
}
\examples{jeux.param <- rbind(weed.factors$binf, weed.factors$nominal, weed.factors$bsup)
weed.simule( jeux.param, sortie=function(x){sum(x[,5])}, nom.sortie="rdt.total")
weed.simule( jeux.param, sortie="annee.finale", nom.sortie="rdt.total")}

