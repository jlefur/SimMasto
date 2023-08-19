# TODO: Add comment
# 
# Author: jlefur
###############################################################################
# 4 juin 2012
###############################################################################
2+3
3^2
a<-2
b=3
4->c
a+b+c
ls()
ls.str()
rm(iris)
ls()
c(1,2,3,4,5)
1:5*4
x=c(1,3,2,10,5)
sum(x)
x=sort(x)
?sum

args(sort)
x
x[2:4]
head(rep(x,100))
m=cbind(1:4,3:5)
dim(m)
solve(m)
c("Pierre","Paul","Jacques")
c("Verts","Marrons","Verts")

listing=data.frame(prenom=c("Pierre","Paul","Jacques"),age=c(30,45,28),yeux=c("Verts","Marrons","Verts"))
summary(listing)
table(listing$yeux)
cheminRessource <- "C:/partage_erwin/2012-EcoleChercheurAnaSensi/TP0/wwdm/"
cheminRessource
nomFichier="serieclim.dat"
cheminNom=paste(cheminRessource,nomFichier,sep="")
data=read.table(cheminNom, header=T)
head(data[,2:3])
setwd("C:/partage_erwin/Workspace_1/firstRproject")
plot(data$ANNEE, data$Tmax)

# ajoute une ligne horizontale à la moyenne
abline(h=mean(data$Tmax),main="Série annuelle des Tmax",col="red")

# demande une fenêtre de deux graphique avec 2 colonnes
par(mfrow=c(1,2))
plot(data$ANNEE, data$Tmin, main="Tmin")
plot(data$ANNEE, data$Tmax, main="Tmax")
data$ANNEE <- as.factor(data$ANNEE)

#  dans Google: R graph gallery opur avoir les exemples et sources des types de graphique
x=seq(-2*pi,2*pi,.1)
plot(x, cos(x))
plot(x,sin(x))

#ajouter un courbe sur le même tracé avec lines et legend
plot(x,sin(x))
lines(x,cos(x),col="red")
legend("topright",c("cos(x)","sin(x)"),lty=1)

#distributions (loi uniforme: min et max)
?Normal
set.seed(1234)
rnorm(10)
plot(rnorm(100,0,1),1:100)
x=seq(-5,5,.1)
plot(x,dnorm(x))
hist(dnorm(x))
hist(rnorm(10000))

#creation de fonctions
somme <- function(a,b){a+b}
somme # -> source de la fonction
for(i in 1:10)print(i)
test = function(){
	i=5;while(i>0){print(i);i=i-1}
	if(i==0)print("c'est normal")}

# import/export de données
write.table(data,"data1.dat")
rm(data)
data = read.table("data1.dat",header=T)

#chargement de la librairie mexico
library('ECmexico2012')

#######################################
# 05 juin 
# TP1 - MORRIS - wwdm
head(wwdm.climates)
wwdm.model(wwdm.factors$nominal,year=9, climat=wwdm.climates)
nbFactors = 7
# Introduire un vecteur contenant 14 valeurs avec c( ,  , …)
planExperience <- matrix( runif(14), nrow=2, ncol=nbFactors, byrow=TRUE) 

planExperience <- matrix( c(wwdm.factors$binf[1:7],wwdm.factors$bsup[1:7],wwdm.factors$nominal[1:7]), 
		nrow=3, ncol=nbFactors, byrow=TRUE)
simule1 <- wwdm.simule(planExperience, year=9, transfo=FALSE)
simule1
# AS Morris (voir pdf TP1_consignes)
Nbtraj <- 10; nbLevels  <- 9; 
gridJump  <- 10 # nombre de pas par saut should be higher than nbLevels
etudeMorris <- morris(model=wwdm.simule,factors=wwdm.factors$name[1:nbFactors],r=Nbtraj,
		design=list(type="oat",levels=nbLevels,grid.jump=gridJump),b1=wwdm.factors$binf,b2=wwdm.factors$bsup,scale=F,transfo=T,year=9)
# avec SimMasto0: faire une matrice avec le plan d'expérience (les variations de tous les paramètres d'entrée) et le résultat de sortie (le vecteur de la variable de sortie)
# 
Nbfac <- 7;nbniv<-1:3;nrep=3
# expand.grid: génère tous les croisements
plancomplet <- expand.grid( Eb = nbniv, Eimax = nbniv, K = nbniv, Lmax = nbniv, A = nbniv, B = nbniv, TI = nbniv, REP = 1:nrep)
N <- nrow(plancomplet)
b1 <- wwdm.factors$binf[1:Nbfac]
b2 <- wwdm.factors$bsup[1:Nbfac]
simu <- t(apply(plancomplet[,1:Nbfac], 1, TP1tirage, b1, b2, 3) )
simu.out <- wwdm.simule(simu,transfo =F, year=9) 
TP1indices.aov = function(table.aov, noms, modeleAOV, titre=''){
# calcul des indices principaux et totaux / noms = vecteur des labels des facteurs / modeleAOV = modèle d'anova créé avec formula() / table.aov = table d'anova issue de la fonction aov()
	vv = terms( modeleAOV, keep.order = F)
	9  # SS = somme des carrés,
	Nbfac = length(noms)
	SS = table.aov[[1]][2]
	neffets = nrow(SS)-1
	SS.fac = SS[1:neffets,]
	SStot = sum(SS.fac)
	vv1 = attr(vv, "factors")
	Itot = rep(NA,Nbfac)
	Iprinc = SS.fac[1:Nbfac]/SStot
	for(i in 1:Nbfac) Itot[i] = sum(SS.fac[vv1[i+1,]==1])/SStot
	M = rbind(Iprinc, Itot-Iprinc)
	barplot( M, col=c("lightblue", "blue"), names.arg=noms) 
	title(titre) 
}
#=======================================
# 06.06.12 - méthodes FAST et SOBL - TP2
Ns  <-  100
ech.fast  <-  fast99(model=NULL,  factors=5,  n=Ns,  M=4,
		q="qunif",  q.arg=list(min=0,max=1))
##  etude  du  resultat:  composants  de  l’objet  cree  et  frequences
print(  names(ech.fast)  )
print(  dim(ech.fast$X)  )
print(  ech.fast$omega  )
##  sequence  s  =  pas  equi-espaces  dans  [0,2pi]
print(  length(ech.fast$s)  )
plot(ech.fast$s,col="red")
abline(h=0)
abline(h=2*pi)
##  sequences  des  valeurs  des  Xi  (ceux  de  frequence  1  puis  celui  de  freq  12)
plot(0.5  +  1/pi  *  asin(sin(ech.fast$omega[2]  *  ech.fast$s)))
lines(0.5  +  1/pi  *  asin(sin(ech.fast$omega[1]  *  ech.fast$s)))
##  representations  2D  des  valeurs  echantillonnees
plot(ech.fast$X) #  ensemble  des  valeurs
plot(ech.fast$X[1:Ns,])  #  valeurs  pour  l’etude  du  facteur  X1
# même type d’étude avec 5 facteurs et Ns = 300
Ns  <-  1025
ech.fast2  <-  fast99(model=NULL,  factors=8,  n=Ns,  M=4,
		q="qunif",  q.arg=list(min=0,max=1))
print(  ech.fast2$omega  )
print(  dim(ech.fast$X)  )
plot(ech.fast2$X[1:Ns,], col="green")

x1 = c(rnorm(30))
x2 = c(rnorm(30))
x3 = x1 * x2

###########################################
# 06.06.12 - TP Isis-Fish 2
rm(list=ls())
setwd("C:/partage_erwin/2012-EcoleChercheurAnaSensi/CleMEXICO2012/Ateliers/Atelier_ISIS_EC2012/DossierAtelierAS_Stagiaire/Rdata")
load("Y2/random_ss_regles_152params_Y2.Rdata")
y2 <- random_ss_regles_152params_Y2
names(y2)

