#############################################################################
# Create WebLogic 12c Domain for WLS 12c Tuning & Troubleshooting workshop
#
# @author Martien van den Akker, Darwin-IT Professionals
# @version 1.0, 2018-01-22
#
#############################################################################
# Modify these values as necessary
import os,sys, traceback
scriptName = sys.argv[0]
#
#Home Folders
fmwHome = os.getenv('FMW_HOME')
javaHome = os.getenv('JAVA_HOME')
wlsHome    = fmwHome+'/wlserver'
domainHome       = domainsHome+'/'+domainName
applicationsHome = applicationsBaseHome+'/'+domainName
#
templateHome=wlsHome+'/common/templates/wls'
baseTemplate=templateHome+'/wls.jar'
#
lineSeperator='__________________________________________________________________________________'
#
def usage():
  print 'Call script as: '
  print 'Windows: wlst.cmd '+scriptName+' -loadProperties localhost.properties'
  print 'Linux: wlst.sh '+scriptName+' -loadProperties environment.properties'
#
# Change Admin Server
def changeAdminServer(adminServerName,listenAddress,listenPort):
  print '\nChange AdminServer'
  print(lineSeperator)
  cd('/Servers/AdminServer')
  # name of adminserver
  print '. Set Name to '+ adminServerName
  set('Name',adminServerName )
  cd('/Servers/'+adminServerName)
  # address and port
  print '. Set ListenAddress to '+ listenAddress
  set('ListenAddress',listenAddress)
  print '. Set ListenPort to '+ str(listenPort)
  set('ListenPort'   ,int(listenPort))
  #
  print 'Create ServerStart'
  create(adminServerName,'ServerStart')
  cd('ServerStart/'+adminServerName)
  #
  cd('/Server/'+adminServerName)
  print 'Create SSL'
  create(adminServerName,'SSL')
  cd('SSL/'+adminServerName)
  set('Enabled'                    , 'False')
  set('HostNameVerificationIgnored', 'True')
#
#
def setLogProperties(logMBeanPath, logFile, fileCount, fileMinSize, rotationType, fileTimeSpan):
  print '\nSet Log Properties for: '+logMBeanPath
  print(lineSeperator)
  cd(logMBeanPath)
  print('Server log path: '+pwd())
  print '. set FileName to '+logFile
  set('FileName'    ,logFile)
  print '. set FileCount to '+str(fileCount)
  set('FileCount'   ,int(fileCount))
  print '. set FileMinSize to '+str(fileMinSize)
  set('FileMinSize' ,int(fileMinSize))
  print '. set RotationType to '+rotationType
  set('RotationType',rotationType)
  print '. set FileTimeSpan to '+str(fileTimeSpan)
  set('FileTimeSpan',int(fileTimeSpan))
#
#
def createServerLog(serverName, logFile, fileCount, fileMinSize, rotationType, fileTimeSpan):
  print('\nCreate Log for '+serverName)
  print(lineSeperator)
  cd('/Server/'+serverName)
  create(serverName,'Log')
  setLogProperties('/Server/'+serverName+'/Log/'+serverName, logFile, fileCount, fileMinSize, rotationType, fileTimeSpan)
#
# Create a Machine
def createMachine(machineName, nmType, nmHost, nmPort):
  cd('/')
  print 'Create a Machine: '+machineName
  create(machineName,'UnixMachine')
  cd('UnixMachine/'+machineName)
  create(machineName,'NodeManager')
  cd('NodeManager/'+machineName)
  set('ListenAddress',nmHost)
  set('ListenPort', int(nmPort))
  set('NMType', nmType)
#
# Create Cluster
def createCluster(cluster):
  print('\nCreate '+cluster)
  print(lineSeperator)
  cd('/')
  create(cluster, 'Cluster')

#
# Create a Managed Server
def createMgdSvr(serverName, listenPort, machineName, cluster):
  cd('/')
  print 'Create ManagedServer: '+serverName
  create(serverName, 'Server')
  cd('/Servers/'+serverName)
  print '. Set listen port to: '+str(listenPort)
  #set('ListenAddress',listenAddress)
  set('ListenPort'   ,int(listenPort))
  set('AutoRestart','True')
  set('AutoKillIfFailed','True')
  set('RestartMax', 2)
  set('RestartDelaySeconds', 10)
  print 'Add ManagedServer '+serverName + ' to machine ' + machineName;
  set('Machine',machineName)
  print 'Add ManagedServer '+serverName + ' to cluster '+ cluster
  assign('Server',serverName,'Cluster',cluster)
#
# Create a boot properties file.
def createBootPropertiesFile(directoryPath,fileName, username, password):
  print('Create Boot Properties File for folder: '+directoryPath)
  serverDir = File(directoryPath)
  bool = serverDir.mkdirs()
  fileNew=open(directoryPath + '/'+fileName, 'w')
  fileNew.write('username=%s\n' % username)
  fileNew.write('password=%s\n' % password)
  fileNew.flush()
  fileNew.close()
#
# Create a base domain.
def createFMWDomain():
  print 'Create Domain';
  print(lineSeperator)
  print('1. Create Base domain '+domainName)
  print('\nCreate base wls domain with template '+baseTemplate)
  print(lineSeperator)
  readTemplate(baseTemplate)
  #
  cd('/')
  # Domain Log
  print('Set base_domain log')
  create('base_domain','Log')
  setLogProperties('/Log/base_domain', logsHome+'/'+domainName+'.log', fileCount, fileMinSize, rotationType, fileTimeSpan)
  #
  # Admin Server
  changeAdminServer(adminServerName,adminListenAddress,adminListenPort)
  createServerLog(adminServerName, logsHome+'/'+adminServerName+'.log', fileCount, fileMinSize, rotationType, fileTimeSpan)
  #
  print('\nSet password in '+'/Security/base_domain/User/weblogic')
  cd('/')
  cd('Security/base_domain/User/weblogic')
  # weblogic user name + password
  print('. Set Name to: ' +adminUser)
  set('Name',adminUser)
  cmo.setPassword(adminPwd)
  #
  if productionMode == 'true':
    print('. Set ServerStartMode to: ' +'prod')
    setOption('ServerStartMode', 'prod')
  else:
    print('. Set ServerStartMode to: ' +'dev')
    setOption('ServerStartMode', 'dev')
  #
  print('write Domain...')
  # write path + domain name
  writeDomain(domainHome)
  closeTemplate()
  #
  createBootPropertiesFile(domainHome+'/servers/'+adminServerName+'/security','boot.properties',adminUser,adminPwd)
  createBootPropertiesFile(domainHome+'/config/nodemanager','nm_password.properties',adminUser,adminPwd)
  #
  es = encrypt(adminPwd,domainHome)
  #
  readDomain(domainHome)
  #
  print('set Domain password for '+domainName) 
  cd('/SecurityConfiguration/'+domainName)
  set('CredentialEncrypted',es)
  #
  print('Set nodemanager password')
  set('NodeManagerUsername'         ,adminUser )
  set('NodeManagerPasswordEncrypted',es )
  #
  cd('/')
  setOption( "AppDir", applicationsHome )
  #
  print('Finished base domain.')
  
  #
  # Section 2: Machine, Cluster, Server(s)
  print('\n2. Extend Base domain with Machine, Cluster and Server(s).')  
  # Create a Machine definition
  createMachine(nodeMgr1MachineName, nodeMgr1Type, nodeMgr1ListenAddress, nodeMgr1ListenPort)
  #
  createCluster(kafkaClr)
  # Create a Managed Server
  createMgdSvr(kafkaSvr1, kafkaSvr1Port, nodeMgr1MachineName, kafkaClr)
  # Create boot properties file for TTServer
  createBootPropertiesFile(domainHome+'/servers/'+kafkaSvr1+'/security','boot.properties',adminUser,adminPwd)
  #  Update the Domain
  updateDomain();
  closeDomain();
#
# Update the Nodemanager Properties
def updateNMProps(nmHome, nodeMgrListenAddress, nodeMgrListenPort, nodeMgrType):
  nmPropertyFile=nmHome+'/nodemanager.properties'
  nmProps = ''
  print ('Read Nodemanager properties file%s: ' % nmPropertyFile)
  f = open(nmPropertyFile)
  for line in f.readlines():
    if line.strip().startswith('ListenPort'):
      line = 'ListenPort=%s\n' % nodeMgrListenPort
    elif line.strip().startswith('ListenAddress'):
      line = 'ListenAddress=%s\n' % nodeMgrListenAddress
    elif line.strip().startswith('SecureListener'):
       if nodeMgrType == 'ssl':
         line = 'SecureListener=true\n'
       else:
         line = 'SecureListener=false\n'
    # making sure these properties are set to true:
    elif line.strip().startswith('QuitEnabled'):
      line = 'QuitEnabled=%s\n' % 'true'
    elif line.strip().startswith('CrashRecoveryEnabled'):
      line = 'CrashRecoveryEnabled=%s\n' % 'true'
    elif line.strip().startswith('weblogic.StartScriptEnabled'):
      line = 'weblogic.StartScriptEnabled=%s\n' % 'true'
    elif line.strip().startswith('weblogic.StopScriptEnabled'):
      line = 'weblogic.StopScriptEnabled=%s\n' % 'true'         
    nmProps = nmProps + line
  # print nmProps
  # Backup file
  nmPropertyFileOrg=nmPropertyFile+'.org'
  print ('Rename File %s to %s ' % (nmPropertyFile, nmPropertyFileOrg))
  os.rename(nmPropertyFile, nmPropertyFileOrg)  
  # Save New File
  print ('\nNow save the changed property file to %s' % nmPropertyFile)
  fileNew=open(nmPropertyFile, 'w')
  fileNew.write(nmProps)
  fileNew.flush()
  fileNew.close()
#
def startDomain():
  print 'Start Nodemanager'
  startNodeManager(verbose='true', NodeManagerHome=nodeMgr1Home, ListenPort=nodeMgr1ListenPort, ListenAddress=nodeMgr1ListenAddress);
  print 'Connect to the Node Manager';
  nmConnect(adminUser, adminPwd, nodeMgr1ListenAddress, nodeMgr1ListenPort, domainName, domainHome, nodeMgr1Type);
  print 'Start AdminServer';
  nmStart(adminServerName);
  print 'Connect to the AdminServer';
  connect(adminUser, adminPwd);
  print 'Start ManagedServer: '+kafkaSvr1;
  start(kafkaSvr1);
#
def main():
  try:
    #
    createFMWDomain()
    #
    updateNMProps(nodeMgr1Home, nodeMgr1ListenAddress, nodeMgr1ListenPort, nodeMgr1Type)
    #
    startDomain()
    #    
    print("\nExiting...")
    exit()
  except:
    apply(traceback.print_exception, sys.exc_info())
    exit(exitcode=1)
#call main()
main()
