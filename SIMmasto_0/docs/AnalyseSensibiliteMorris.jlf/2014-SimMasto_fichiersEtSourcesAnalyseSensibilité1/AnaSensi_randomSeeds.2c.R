# TODO: Add comment
# 
# Author: jlefur, 09,10.2012
###############################################################################
library(ECmexico2012)
#FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
#  FONCTIONS
#FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
## JLF: function view : displays successive graphs and prints of the study
## @see the corresponding sources at the bottom of the file - 07.09.2012
view <- function(etude.morris) {
	print(head(etude.morris$ee,3))
	print(head(etude.morris$X,3))
	## CB Q3 : Analyse de l'échantillonnage de l'espace des facteurs (à répéter avec r=20, r=50, r=100)
	print("TP1histo(etude.morris)")
	TP1histo(etude.morris)
	## CB Q4 :  Exploration graphique de l'influence des facteurs sur la sortie
	b1 <- etude.morris$binf[1:Nbfac]
	b2 <- etude.morris$bsup[1:Nbfac]
	# JLF: b1 & b2 utilisés ssi transfo=T
	print("TP1corr(etude.morris, transfo=T, binf=b1, bsup=b2 )")
	TP1corr(etude.morris, transfo=T, binf=b1, bsup=b2 )
	## CB Q5 : Analyse des sorties
	print("plot(etude.morris)")
	par(mfrow=c(1,1),ask=T)
	plot(etude.morris)
	## CB Q6 :  Calculer les IC à 95% des indices de Morris
	TP1.ICmorris(etude.morris)# JLF: ne semble fonctionner qu'avec $X et/ou $ee normalisées ?
}
#FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
# JLF: adaptation de la fonction TP1.ICmorris(etude.morris) de CB (cf. fonction view).
ICmorris <- function (etude.morris, titre) {
	noms = etude.morris$factors
	Nbfac = length(noms)
	Nbtraj = etude.morris$r
	sigma = sqrt(apply(etude.morris$ee, 2, var))
	IC.mu = apply(abs(etude.morris$ee), 2, t.test)
	tabICmu = matrix(0, Nbfac, 2)
	tabICsig = matrix(0, Nbfac, 2)
	rownames(tabICmu) = noms
	rownames(tabICsig) = noms
	for (i in 1:Nbfac) {
		tabICmu[i, ] = IC.mu[[i]]$conf.int
		tabICsig[i, ] = c(sigma[i] * (Nbtraj - 1)^0.5/qchisq(0.975, 
						Nbtraj - 1)^0.5, sigma[i] * (Nbtraj - 1)^0.5/qchisq(0.025, 
						Nbtraj - 1)^0.5)
	}
	print(paste(titre,": intervalle de confiance mu*"))
	print(tabICmu)
	print(paste(titre,": intervalle de confiance sigma"))
	print(tabICsig)
	mu.star <- apply(etude.morris$ee, 2, function(x) mean(abs(x)))
	xlim1 = c(min(tabICmu[, 1]), max(tabICmu[, 2]))
	ylim1 = c(min(tabICsig[, 1]), max(tabICsig[, 2]))
	print("xlim1 - ylim1")
	print(xlim1)
	print(ylim1)
	plot(etude.morris, xlim = xlim1, ylim = ylim1, main=titre)
	plot(mu.star, sigma, xlim = xlim1, ylim = ylim1, main=titre)
	for (i in 1:Nbfac) {
		segments(tabICmu[i, 1], sigma[i], tabICmu[i, 2], sigma[i], 
				col = "red")
		segments(mu.star[i], tabICsig[i, 1], mu.star[i], tabICsig[i, 
						2], col = "red")
	}
}
#FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
#  FIN DES FONCTIONS
#FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF

#définition des paramètres
	morris.r <- 60 # nombre de trajectoires
	morris.levels  <- 6 # nombre de niveaux (nombre de valeurs différentes pour un paramètre)
	morris.grid.jump  <- 3 # type de déplacement
	#lecture des facteurs
	setwd("C:/Documents and Settings/jlefur/Bureau/2012-05_SMaCH/Workspace_SMaCH/Morris-1")
	seeds.factors <- data.frame(read.csv("20121009-SimMasto0-SeedsFactors.txt",header=TRUE,sep=";",check.names=TRUE, as.is=TRUE,stringsAsFactors=TRUE))
	noms <- seeds.factors$name
	Nbfac <- length(noms)
	
# CONSTRUCTION DU PLAN D'EXPERIENCE
# ++++++++++++++++++++++++++++++++++
#calcul du plan d'expérience par morris
morris.seeds <- morris(model = NULL, factors = seeds.factors$name[1:Nbfac] ,
		r = morris.r ,scale=FALSE,
		design = list(type = "oat", levels = morris.levels, grid.jump= morris.grid.jump)
)
binf <- seeds.factors$binf[1:Nbfac]
bsup <- seeds.factors$bsup[1:Nbfac]
#reconstruction du plan à partir de la matrice X normalisée
planExperience <- t(binf + t(morris.seeds$X) * (bsup - binf))
print(head(planExperience))
#sauvegarde (i) de la matrice X pour réutilisation après simulations et (ii) du plan d'expérience (pour mémoire)
write.table(morris.seeds$X,file="morris.seeds.matriceX.csv",sep=";",col.names = NA ,row.names = TRUE )
write.table(planExperience,file="morris.seeds.planExperience.csv",sep=";",col.names = NA ,row.names = TRUE )


# Ecriture des fichiers de parameters.xml utilisés par le batch de repast Simphony et traduisant le plan d'expérience de Morris.
# On réécrit un fichier xml en construisant la chaine de caractères définissant la syntaxe correcte pour chaque constante.
# Author: jlefur, 09.2012
# ++++++++++++++++++++++++++++++++++

#chaines fixes de caractères pour l'écriture du fichier xml
c1<-"<parameter constant_type=\""
c3<-"\" name=\""
c5<-"\" type=\"constant\" value=\""
c7<-"\"/>"
planExperience <- data.frame(planExperience)
nbRuns <- nrow(planExperience)
if (file.exists("param"))print("répertoire params existe. L'effacer et recommencer") else dir.create("params")
constant_type <- "number" # random seeds all have a constant_type equal to number
genericFilename <- "params/batch_params" #nom générique des instances des fichiers xml

	#Pour chaque ligne: construire le nom de fichier out/lire les paramètres/écrire la chaine de caractères correspondante
for (iiemeRun in 0:(nbRuns-1)) {
	# ouverture du fichier parametre xml de sortie
	filename <- paste(genericFilename,iiemeRun,".xml",collapse=NULL,sep="")
	paramsOutFile <- file(filename, 'w') 
	# lecture et écriture des paramètres précédents du fichier xml
	paramsInFile <- file('batch_params_BASE.xml', 'r')
	while (length(input <- readLines(paramsInFile, n=1000)) > 0){
		for (iString in 1: length (input)) {
			if(length(grep("</sweep>",input[iString]))==0)
			{
				output <- input[iString]
				writeLines(output, con=paramsOutFile)
			}
		}
	}
	one_plan <-planExperience[iiemeRun+1,]
	# composition et écriture de la chaine de caractère, nième constante issue du plan d'expérience
	for(jemeFacteur in 1:Nbfac){
		constant_name <-seeds.factors$name[jemeFacteur]
		constant_value <-planExperience[iiemeRun+1,jemeFacteur]
		chaine <- paste0(c1,constant_type,c3,constant_name,"_RANDOM_SEED",c5,format(constant_value,scientific=FALSE),c7)#, collapse=NULL)
		writeLines(chaine, con=paramsOutFile)
	}
	# écriture de la fin du fichier et fermeture
	writeLines("</sweep>",con=paramsOutFile)
	close(paramsOutFile)
	close(paramsInFile)
}

# RE-INJECTION DES RESULTATS APRES SIMULATION
# +++++++++++++++++++++++++++_____+++++++++++
#lecture des facteurs
setwd("C:/Documents and Settings/jlefur/Bureau/2012-05_SMaCH/Workspace_SMaCH/Morris-1")
morris.seeds <- morris(model = NULL, factors = seeds.factors$name[1:Nbfac] ,
		r = morris.r ,scale=FALSE,
		design = list(type = "oat", levels = morris.levels, grid.jump= morris.grid.jump)
)
XFromFile<-as.matrix(read.table(file="20121010-BurrowCentreN1000.matriceX.csv",sep=";",header = TRUE,row.names=1))
nbRuns <- nrow(XFromFile)
morris.seeds$X <- XFromFile
#compilation des outputs y
y<-data.frame()
setwd("DonneesAlleliques-20121010-AnaSensi-burrowCentre")
for (iemeRun in 0:(nbRuns-1)) {
	# lecture des fichiers de sortie
	print(paste(iemeRun,"IndicateursReduit.csv",collapse=NULL,sep=""))
	output_i <- read.table(paste(iemeRun,"IndicateursReduit.csv",collapse=NULL,sep=""),sep=";",header=TRUE,check.names=TRUE, as.is=TRUE,stringsAsFactors=TRUE,row.names=1)
	Nbligne <- nrow(output_i)
	Nbcol <- ncol(output_i)
	# select last line of the csv output from SimMasto
	input<-output_i[Nbligne,]# <<<<<<<<<<<<<<<<<< TODO number in source
	for (oneIndicator in 1:Nbcol)y[iemeRun+1,oneIndicator]<-input[oneIndicator]# un par un car problème pour mettre toute la ligne d'un coup
	}
#définition des variables
names <-c("Occupancy","HeteroObservee","HeteroAttendue","FIS","RichAllMoy","TaillePop","SexeRatio","NbBurrows","NumRun")
par(ask=TRUE)
#pdf("muStar-sigma.pdf")
for (i_plot in list(1,4,5,6,7,8)) {
#for (i_plot in 1:(length(names(y))-1)) {
	morris.seeds$y <-y[,i_plot]
	print(paste("plotting: ",names(y)[i_plot]))
	tell(morris.seeds,y[,i_plot])
	#plot(morris.seeds, main=names(y)[i_plot])
	#ICmorris(morris.seeds,names(y)[i_plot] )
	view(morris.seeds)
}
#dev.off()

#view(morris.seeds)

# ----------------- END ------------------------
# ----------------- END ------------------------
# ----------------- END ------------------------

