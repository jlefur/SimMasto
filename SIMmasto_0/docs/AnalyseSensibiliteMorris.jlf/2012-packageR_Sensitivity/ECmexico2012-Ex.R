pkgname <- "ECmexico2012"
source(file.path(R.home("share"), "R", "examples-header.R"))
options(warn = 1)
library('ECmexico2012')

assign(".oldSearch", search(), pos = 'CheckExEnv')
cleanEx()
nameEx("TP1pavage")
### * TP1pavage

flush(stderr()); flush(stdout())

### Name: TP1pavage
### Title: Construction du plan avec tirage dans les pav\'es d\'efini par
###   un plan P...
### Aliases: TP1pavage

### ** Examples
TP1pavage( rbind(1:3,c(2,2,2)),binf=c(-10,0,100),bsup=c(10,5,600),Nbclass=5)


cleanEx()
nameEx("TP1tirage")
### * TP1tirage

flush(stderr()); flush(stdout())

### Name: TP1tirage
### Title: Tirage uniforme dans un pav\'e de R^K...
### Aliases: TP1tirage

### ** Examples
TP1tirage( PAV=c(1,2,3),binf=c(-10,0,100),bsup=c(10,5,600),Nbclass=5)


cleanEx()
nameEx("convertU2N")
### * convertU2N

flush(stderr()); flush(stdout())

### Name: convertU2N
### Title: Transformation d'un \'echantillon d'une loi uniforme vers une
###   loi normale...
### Aliases: convertU2N

### ** Examples
convertU2N(seq(8,10,length=11),fungus.factors["Topt",])


cleanEx()
nameEx("convertfrom.basep")
### * convertfrom.basep

flush(stderr()); flush(stdout())

### Name: convertfrom.basep
### Title: Utilitaire plans fractionnaires: conversion base p vers base
###   10...
### Aliases: convertfrom.basep

### ** Examples
conv.into2 <- convertinto.basep( x=c(0:16), p=2 )
convertfrom.basep(x=conv.into2, p=2)


cleanEx()
nameEx("convertinto.basep")
### * convertinto.basep

flush(stderr()); flush(stdout())

### Name: convertinto.basep
### Title: Utilitaire plans fractionnaires: conversion base 10 vers base
###   p...
### Aliases: convertinto.basep

### ** Examples
convertinto.basep( x=c(0:16), p=2 )
convertinto.basep( x=c(0:16), p=3 )


cleanEx()
nameEx("crossing")
### * crossing

flush(stderr()); flush(stdout())

### Name: crossing
### Title: G\'en\'eration d'un plan factoriel complet \'a partir des
###   nombres de modalit\'es de...
### Aliases: crossing

### ** Examples
crossing( c(2,3,4), start=0 )


cleanEx()
nameEx("fungus.model")
### * fungus.model

flush(stderr()); flush(stdout())

### Name: fungus.model
### Title: Mod\'ele "fungus" (croissance champignon selon l'humidit\'e)...
### Aliases: fungus.model

### ** Examples
fungus.model( fungus.factors$nominal, temperature=c(10,15,18,21,30) )


cleanEx()
nameEx("fungus.simule")
### * fungus.simule

flush(stderr()); flush(stdout())

### Name: fungus.simule
### Title: Simulation du mod\'ele "fungus"...
### Aliases: fungus.simule

### ** Examples
scenarios <- rbind(fungus.factors$binf,fungus.factors$nominal,fungus.factors$bsup)
fungus.simule( scenarios, temperature=c(10,15,18,21,30) )


cleanEx()
nameEx("inverses.basep")
### * inverses.basep

flush(stderr()); flush(stdout())

### Name: inverses.basep
### Title: Calcul basique des inverses modulo p...
### Aliases: inverses.basep

### ** Examples
inverses.basep(5)
(inverses.basep(17) * (1:16)) %%17


cleanEx()
nameEx("ishigami.model")
### * ishigami.model

flush(stderr()); flush(stdout())

### Name: ishigami.model
### Title: Mod\'ele d'Ishigami, d\'ecrit dans Saltelli et al., 2000
### Aliases: ishigami.model

### ** Examples
ishigami.model( c(-1,0,-1) )
ishigami.model( rbind( c(1,1,1),c(-1,0,-1) )  )


cleanEx()
nameEx("ishigami.simule")
### * ishigami.simule

flush(stderr()); flush(stdout())

### Name: ishigami.simule
### Title: Simulation du mod\'ele d'Ishigami, d\'ecrit dans Saltelli et
###   al., 2000
### Aliases: ishigami.simule

### ** Examples
ishigami.simule( c(-1,0,-1) )
ishigami.simule( rbind( c(1,1,1),c(-1,0,-1) )  )


cleanEx()
nameEx("lhs.plan")
### * lhs.plan

flush(stderr()); flush(stdout())

### Name: lhs.plan
### Title: Tire selon le plan hyper-cube latin un \'echantillon de valeurs
###   de param\'etres...
### Aliases: lhs.plan

### ** Examples
lhs.plan( taille = 10, plage = fungus.factors, repet = NULL, tout = FALSE)
lhs.plan( taille = 10, plage = wwdm.factors[1:7,],
                       repet = wwdm.factors[8,], tout = FALSE)


cleanEx()
nameEx("lhs2intervalle")
### * lhs2intervalle

flush(stderr()); flush(stdout())

### Name: lhs2intervalle
### Title: Projection de valeurs tir\'ees entre 0 et 1 sur une autre plage
###   de variation...
### Aliases: lhs2intervalle

### ** Examples
## Not run: TODO


cleanEx()
nameEx("loiGeneriqueTronquee")
### * loiGeneriqueTronquee

flush(stderr()); flush(stdout())

### Name: loiGeneriqueTronquee
### Title: Fonctions g\'en\'eriques pour loi tronqu\'ee
### Aliases: loiGeneriqueTronquee loiGeneriqueTronquee d.trunc.distr
###   p.trunc.distr q.trunc.distr r.trunc.distr LoiGeneriqueTronquee

### ** Examples

d.trunc.distr(x=c(-2.5,-1.96,-1,0,1,1.96,2.5), distr = 'norm',
                         trunc.int = c(-2, 2), mean = 0, sd = 1)
d.trunc.distr(x=c(-2.5,-1.96,-1,0,1,1.96,2.5),
              distr = c('dnorm', 'pnorm', 'qnorm', 'rnorm'),
              trunc.int = c(-2, 2), mean = 0, sd = 1)


cleanEx()
nameEx("perspPlus")
### * perspPlus

flush(stderr()); flush(stdout())

### Name: perspPlus
### Title: Interface conviviale pour des graphiques pour 3 variables...
### Aliases: perspPlus

### ** Examples
#perspPlus(x=Tmax,y=Tmin,z = Y10, pcol=c("blue", "green"), pphi=30, ptheta=-30,
#     nomx=deparse(substitute(x)),nomy=deparse(substitute(y)),
#     nomz=deparse(substitute(z)),type=1)


cleanEx()
nameEx("regular.fraction")
### * regular.fraction

flush(stderr()); flush(stdout())

### Name: regular.fraction
### Title: Construction de plans factoriels fractionnaires sym\'etriques
###   entre les facteurs.
### Aliases: regular.fraction

### ** Examples
regular.fraction(s=8, p=2, r=4, resolution=4)
regular.fraction(s=9, p=2, r=4, resolution=4)


cleanEx()
nameEx("weed.model")
### * weed.model

flush(stderr()); flush(stdout())

### Name: weed.model
### Title: Mod\'ele "Weed" pour un jeu de param\'etres et un jeu de
###   d\'ecisions
### Aliases: weed.model

### ** Examples
decision <- data.frame(Soil=c(0,1),Crop=c(0,1),Herb=c(0,1))
weed.model( weed.factors$nominal, decision=decision )


cleanEx()
nameEx("weed.simule")
### * weed.simule

flush(stderr()); flush(stdout())

### Name: weed.simule
### Title: Simulations en s\'erie du mod\'ele "Weed"
### Aliases: weed.simule

### ** Examples
jeux.param <- rbind(weed.factors$binf, weed.factors$nominal, weed.factors$bsup)
weed.simule( jeux.param, sortie=function(x){sum(x[,5])}, nom.sortie="rdt.total")
weed.simule( jeux.param, sortie="annee.finale", nom.sortie="rdt.total")


cleanEx()
nameEx("wwdm.model")
### * wwdm.model

flush(stderr()); flush(stdout())

### Name: wwdm.model
### Title: Mod\'ele "wwdm" pour un jeu de param\'etres
### Aliases: wwdm.model wwdm.model

### ** Examples
#data()
#wwdm.model()
#sum( wwdm.model() )  #biomasse cumulee
#wwdm.model(param=wwdm.factors$nominal, year=NULL, climate=wwdm.climates)
#wwdm.model(param=wwdm.factors$nominal, year=5)


cleanEx()
nameEx("wwdm.simule")
### * wwdm.simule

flush(stderr()); flush(stdout())

### Name: wwdm.simule
### Title: Simulations en s\'erie du mod\'ele "wwdm"
### Aliases: wwdm.simule wwdm.simule

### ** Examples
jeux.parametres <- as.data.frame(rbind(wwdm.factors$binf,
                     wwdm.factors$nominal, wwdm.factors$bsup))
names(jeux.parametres) <- wwdm.factors$name
wwdm.simule(jeux.parametres)


### * <FOOTER>
###
cat("Time elapsed: ", proc.time() - get("ptime", pos = 'CheckExEnv'),"\n")
grDevices::dev.off()
###
### Local variables: ***
### mode: outline-minor ***
### outline-regexp: "\\(> \\)?### [*]+" ***
### End: ***
quit('no')
