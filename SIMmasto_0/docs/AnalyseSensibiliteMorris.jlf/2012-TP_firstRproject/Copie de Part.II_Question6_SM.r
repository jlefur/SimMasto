###############################################################################
### Partie II. question 5
###############################################################################
### Chargement des Rdata
# entrer ici le chemin vers le dossier Rdata
setwd("G:/DossiersASynchroniser/AnalysesSensibilite/AtelierAFH/Rdata/")
# que fait la fonction ?
source(regularfractions.r)

###########
lesPlans <- c("factfract4_7param_res4","factfract5_7param_res4","factfract6_7param_res7")
for (plan in lesPlans){
  for(i in 1:6)
  {
  
    load(paste(plan,"_ss_regles_Y",i,".Rdata",sep=""))
    load(paste(plan,"_MPA_Y",i,".Rdata",sep=""))
    load(paste(plan,"_TAC_Y",i,".Rdata",sep=""))
    
    # on les renomme pour chaque strategie de gestion
    ssr = eval(parse(text=paste(plan,"_ss_regles_Y",i,sep="")))
    amp = eval(parse(text=paste(plan,"_MPA_Y",i,sep="")))
    tac = eval(parse(text=paste(plan,"_TAC_Y",i,sep="")))
  
  
  
  # pour chaque mesure de gestion
      res_ssr = ssr$analysis_result[[2]]
      print(res_ssr)
      jpeg(filename = paste("G:/DossiersASynchroniser/AnalysesSensibilite/AtelierAFH/Graphiques/PIIQ6_",plan,"_ss_regles_Y",i,".jpg",sep=""))
      par(las=2,mar=c(15,3,2,2))
      barplot(res_ssr, las=2)
      title(main = paste(plan," SS Regle Y",i,sep=""))
      
      res_amp = amp$analysis_result[[2]]
      print(res_amp)
      jpeg(filename = paste("G:/DossiersASynchroniser/AnalysesSensibilite/AtelierAFH/Graphiques/PIIQ6_",plan,"_AMP_Y",i,".jpg",sep=""))
      par(las=2,mar=c(15,3,2,2))
      barplot(res_amp, las=2)
      title(main = paste(plan," AMP Y",i,sep=""))
      
      res_tac = tac$analysis_result[[2]]
      print(res_tac)
      jpeg(filename = paste("G:/DossiersASynchroniser/AnalysesSensibilite/AtelierAFH/Graphiques/PIIQ6_",plan,"_TAC_Y",i,".jpg",sep=""))
      par(las=2,mar=c(15,3,2,2))
      barplot(res_tac, las=2)
      title(main = paste(plan," TAC Y",i,sep=""))
      dev.off()
    }
  }
