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
# Start the domain
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
    startDomain()
    #    
    print("\nExiting...")
    exit()
  except:
    apply(traceback.print_exception, sys.exc_info())
    exit(exitcode=1)
#call main()
main()
