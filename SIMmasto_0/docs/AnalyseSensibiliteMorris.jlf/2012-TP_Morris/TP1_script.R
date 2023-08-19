### ECOLE-CHERCHEUR MEXICO, GIENS 2012
### SCRIPTS DU TP1

## TP1 M\'ethodes de criblage par discr\'etisation de l’espace
## Auteur	:	Claude Bruchou
## 		Unit\'e de Biostatistique et processus Spatiaux (BioSP)
## 		INRA Avignon (France)

## Preliminaires
library(ECmexico2012)
library(Rcmdr)

help(wwdm.model)
help(wwdm.simule)
help(wwdm.climates)
wwdm.factors

?morris

## Q1 :   cr\'eer une matrice X \`a 2 lignes et 7 colonnes contenant des valeurs
## des facteurs Eb, Eimax, K, Lmax, A, B et TI  puis lancer l'ex\'ecution de
## la FC:

Nbfac = 7

# Introduire un vecteur contenant 14 valeurs avec c( ,  , …)
# N.B : le remplissage de la matrice X est effectu\'e par ligne (byrow=T)
# Par exemple: on tire les scenarios au hasard sous leur forme codee entre 0 et 1

set.seed(123)
scenarios.random <- runif(14)
X <- matrix( scenarios.random, nrow=2, ncol=Nbfac, byrow=TRUE)
colnames(X)=wwdm.factors$name[1:Nbfac]
X <- matrix( scenarios.random, nrow=2, ncol=Nbfac, byrow=TRUE)
colnames(X)=wwdm.factors$name[1:Nbfac]
y <- wwdm.simule(X, year = 9,transfo=TRUE)
print(y)

# Variante 2012: introduire trois scenarios constitues des bornes inf et sup et des
#                valeurs nominales

scenario1 <- wwdm.factors$binf[1:7]
scenario2 <- wwdm.factors$nominal[1:7]
scenario3 <- wwdm.factors$bsup[1:7]

X2 <- matrix( c(scenario1,scenario2,scenario3), nrow=3, ncol=Nbfac, byrow=T)
colnames(X2)=wwdm.factors$name[1:Nbfac]

y2 <- wwdm.simule(X2, year = 9,transfo=FALSE)
print(y2)

## AS selon la m\'ethode de Morris
## Q2
Nbtraj <- 10
Nbniv  <- 6
delta  <- 3
etude.morris <- morris(model = wwdm.simule, factors = wwdm.factors$name[1:Nbfac] ,
                       r = Nbtraj ,scale=FALSE,
			design = list(type = "oat", levels = Nbniv, grid.jump= delta),
			transfo = TRUE,
			b1=wwdm.factors$binf[1:Nbfac],
			b2=wwdm.factors$bsup[1:Nbfac],
			year=9)

## Q3 : Analyse de l'\'echantillonnage de l'espace des facteurs
TP1histo(etude.morris)
## a repeter avec r=20, r=50, r=100


##  Q4 :  Exploration graphique de l'influence des facteurs sur la sortie
b1 <- wwdm.factors$binf[1:Nbfac]
b2 <- wwdm.factors$bsup[1:Nbfac]
TP1corr(etude.morris, transfo=T, binf=b1, bsup=b2 )

##  Q5 : Analyse des sorties
par(mfrow=c(1,1),ask=T)
plot(etude.morris)


##  Q6 :  Calculer les IC \`a 95% des indices de Morris
TP1.ICmorris( etude.morris)

##
##  AS avec  Plan fractionnaire et Anova
##
##  G\'en\'eration d'un plan fractionnaire
##  Q7 :
Nbfac <- 7
# 1er essai
r <- 5
plan7.2.V <- regular.fraction(s=Nbfac, p=2, r=r, resolution=5)$plan
# pas de solution, 2eme essai
r <- 6
plan7.2.V <- regular.fraction(s=Nbfac, p=2, r=r, resolution=5)$plan
colnames(plan7.2.V) <- wwdm.factors$name[1:Nbfac]

##
##  Plan fractionnaire et pavage
##
##  Q8 :
plan7.2.V <- plan7.2.V + 1
Nbfac <- 7

# planfrac.pav  est une liste ayant pour composantes P.out (facteurs cod\'es)
#  et xx (coordonn\'ees des points)
planfrac.pav <- TP1pavage(P = plan7.2.V, nrep =3, Nbclass =2,
                          binf =  wwdm.factors$binf[1:Nbfac],
                          bsup = wwdm.factors$bsup[1:Nbfac])

##  Q9 :
planfrac.out <- wwdm.simule(planfrac.pav$xx, transfo = FALSE, year=9)

## N.B. :  TP1pavage fournit le codage ad\'equat des facteurs, d'o\`u transfo=F.

##
##  Analyse de variance
##

##  Q10 :
Nbfac <- 7

##  codage des facteurs du plan en objet factor pour aov()
planfrac.fac <- lapply(data.frame(planfrac.pav$P.out), as.factor)
planfrac.dat <- data.frame(planfrac.fac,  Y = planfrac.out)


##  Q11 :
## \'ecriture du mod\`ele d'anova avec effets principaux et interaction d'ordre 2
mod.aov1 <- formula( Y ~ (Eb + Eimax + K +  Lmax +  A + B + TI)^2)
planfrac.aov <- aov(mod.aov1, planfrac.dat)
planfrac.table <- summary(planfrac.aov)

##  Q13 :
interaction.plot( planfrac.dat$?? , planfrac.dat$??, planfrac.dat$Y)

##
##  Indices de sensibilit\'e
##  Q14 :
##
indices.aov <- TP1indices.aov(planfrac.table,
              noms = wwdm.factors$name[1:Nbfac],
              modeleAOV = mod.aov1,
              titre='Plan Fractionnaire')

##
##  AS avec Plan complet et Anova
##
##  Q15 :
Nbfac <- 7
nbniv <- 1:3 ; nrep =3
plancomplet <- expand.grid( Eb = nbniv, Eimax = nbniv, K = nbniv,
			Lmax = nbniv, A = nbniv, B = nbniv,
			TI = nbniv, REP = 1:nrep)
N <- nrow(plancomplet)
b1 <- wwdm.factors$binf[1:Nbfac]
b2 <- wwdm.factors$bsup[1:Nbfac]
simu <- t(apply(plancomplet[,1:Nbfac], 1, TP1tirage, b1, b2, 3) )
simu.out <- wwdm.simule(simu,transfo =F, year=9)

##
##  Analyse de la variance de la sortie de la FC
##

##  Q16 :
# transformation de la matrice en objet factor pour aov()
plancomplet.fac <- lapply(data.frame(plancomplet), as.factor)
plancomplet.dat <- data.frame(plancomplet.fac,  Y = simu.out)
# formule du mod\`ele d'anova :
mod.aov2 <- formula(Y ~ (Eb + Eimax + K +  Lmax +  A + B + TI)^4)
plancomplet.aov <- aov(mod.aov2, plancomplet.dat )

##  Q17 :
plancomplet.table <- summary(plancomplet.aov)
##  Q18 :
interaction.plot( plancomplet.dat$?? , plancomplet.dat$??, plancomplet.dat$Y)

##
##  Indices de sensibilit\'e
##

##  Q19 :  Calculer les indices de sensibilit\'e issus de l'anova du plan complet.
indices.aov <- TP1indices.aov(plancomplet.table,
                             noms = wwdm.factors$name[1:Nbfac],
                             modeleAOV = mod.aov2, titre='Plan Complet')

##
##  Conclusions
##

##  Q20 : comparer les r\'esultats issus des trois analyses pr\'ec\'edentes.

## --------------------------- FIN du TP 1 -----------------------------
