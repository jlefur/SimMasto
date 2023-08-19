###############################################################################
### Partie I. question 3
###############################################################################
### Chargement des Rdata
# entrer ici le chemin vers le dossier Rdata
setwd("C:/partage_erwin/2012-EcoleChercheurAnaSensi/CleMEXICO2012/Ateliers/Atelier_ISIS_EC2012/DossierAtelierAS_Stagiaire/Rdata/Y2")
#setwd("G:/DossiersASynchroniser/AnalysesSensibilite/AtelierAFH/Rdata/")

for(i in 2)
{

  load(paste("random_ss_regles_152params_Y",i,".Rdata",sep=""))
  load(paste("random_MPA_152params_Y",i,".Rdata",sep=""))
  load(paste("random_TAC_152params_Y",i,".Rdata",sep=""))
  
  # on les renomme pour chaque strategie de gestion
  ssr = eval(parse(text=paste("random_ss_regles_152params_Y",i,sep="")))
  amp = eval(parse(text=paste("random_MPA_152params_Y",i,sep="")))
  tac = eval(parse(text=paste("random_TAC_152params_Y",i,sep="")))

    print(paste("Pour Y_",i,sep=""))
    print("Pour SSR")
    ssr_table.aov=ssr$analysis_result
    SS_ssr =  summary(ssr_table.aov[[1]])[[1]][,2]
    names(SS_ssr) = dimnames(ssr$isis.simule)[[2]][1:152] 
    nbeffets_ssr = length(SS_ssr)-1
    SS.fac_ssr=SS_ssr[1:nbeffets_ssr]
    SStot_ssr = sum(SS.fac_ssr)
    nbfac_ssr<-nbeffets_ssr
    Itot_ssr = rep(NA,nbfac_ssr)
    Iprinc_ssr= SS.fac_ssr[1:nbfac_ssr]/SStot_ssr
    print(Iprinc_ssr[Iprinc_ssr>0.01])
    
    print("Pour AMP")
    amp_table.aov=amp$analysis_result
    SS_amp =  summary(amp_table.aov[[1]])[[1]][,2]
    names(SS_amp) = dimnames(amp$isis.simule)[[2]][1:152]
    nbeffets_amp = length(SS_amp)-1
    SS.fac_amp=SS_amp[1:nbeffets_amp]
    SStot_amp = sum(SS.fac_amp)
    nbfac_amp<-nbeffets_amp
    Itot_amp = rep(NA,nbfac_amp)
    Iprinc_amp= SS.fac_amp[1:nbfac_amp]/SStot_amp
    print(Iprinc_amp[Iprinc_amp>0.01])#ICI
    
    print("Pour TAC") 
    names(Iprinc_amp[Iprinc_amp])
    tac_table.aov=tac$analysis_result
    SS_tac =  summary(tac_table.aov[[1]])[[1]][,2]
    names(SS_tac) = dimnames(tac$isis.simule)[[2]][1:152]
    nbeffets_tac = length(SS_tac)-1
    SS.fac_tac=SS_tac[1:nbeffets_tac]
    SStot_tac = sum(SS.fac_tac)
    nbfac_tac<-nbeffets_tac
    Itot_tac = rep(NA,nbfac_tac)
    Iprinc_tac= SS.fac_tac[1:nbfac_tac]/SStot_tac
    print(Iprinc_tac[Iprinc_tac>0.01])#ICI
    
    jpeg(filename = paste("Graphiques/PIQ3_aovIndices",i,".jpeg",sep=""), width = 900)
    par(mfrow = c(1,3))
    mp=barplot(Iprinc_ssr,axisnames=FALSE,main="Sans règle")
    axis(side =1,pos=0, at=mp,tick=FALSE,labels= gsub("nephrops.",'',names(Iprinc_ssr)),las=2,cex.axis=0.8,hadj=0)
    mp=barplot(Iprinc_amp,axisnames=FALSE,main="Sans règle")
    axis(side =1,pos=0, at=mp,tick=FALSE,labels= gsub("nephrops.",'',names(Iprinc_amp)),las=2,cex.axis=0.8,hadj=0)
    mp=barplot(Iprinc_tac,axisnames=FALSE,main="Sans règle")
    axis(side =1,pos=0, at=mp,tick=FALSE,labels= gsub("nephrops.",'',names(Iprinc_tac)),las=2,cex.axis=0.8,hadj=0)
    
  dev.off()
    
    
    
    
    
}

