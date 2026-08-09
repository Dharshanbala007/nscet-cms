; NSCET CMS - Inno Setup Script
; Run with Inno Setup Compiler (iscc.exe)

#define MyAppName "NSCET College Management System"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "Nadar Saraswathi College of Engineering and Technology"
#define MyAppURL "https://www.nscet.edu.in"
#define MyAppExeName "NSCET-CMS.exe"

[Setup]
AppId={{B5E3F8A2-7C4D-4E6F-9A1B-2D3C4E5F6A7B}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
DefaultDirName={autopf}\NSCET-CMS
DefaultGroupName={#MyAppName}
OutputDir=installer-output
OutputBaseFilename=NSCET-CMS-Setup-{#MyAppVersion}
SetupIconFile=icons\nscet-icon.ico
Compression=lzma2/ultra64
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=admin
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
MinVersion=6.1.7601

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
; Main application JAR
Source: "target\nscet-cms-1.0.0.jar"; DestDir: "{app}\app"; Flags: ignoreversion

; Modules JARs
Source: "target\cms-db-1.0.0.jar"; DestDir: "{app}\app"; Flags: ignoreversion
Source: "target\cms-core-1.0.0.jar"; DestDir: "{app}\app"; Flags: ignoreversion
Source: "target\cms-ui-1.0.0.jar"; DestDir: "{app}\app"; Flags: ignoreversion
Source: "target\cms-reports-1.0.0.jar"; DestDir: "{app}\app"; Flags: ignoreversion

; Dependencies
Source: "target\lib\*"; DestDir: "{app}\app\lib"; Flags: ignoreversion recursesubdirs createallsubdirs

; Configuration
Source: "config\*"; DestDir: "{app}\config"; Flags: ignoreversion recursesubdirs createallsubdirs

; MySQL Connector
Source: "target\lib\mysql-connector-j*.jar"; DestDir: "{app}\app\lib"; Flags: ignoreversion

; First-run setup script
Source: "scripts\first-run.bat"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon

[Run]
Filename: "{app}\first-run.bat"; Description: "Run first-time setup (configure database)"; Flags: postinstall skipifsilent

[UninstallDelete]
Type: filesandordirs; Name: "{app}"
