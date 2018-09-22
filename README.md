# QLRI (Qubic Lite Reference Implementation)

This runnable program provides a user-interface to perform basic interactions with the Qubic Lite protocol.
Create qubics, run oracles, read quorum based results etc.

## Downloading the Precompiled Jar File (Simple Installation)

Simply download the newest .jar file from the [releases](https://github.com/qubiclite/qlri/releases) and put it into your favorite folder/directory. After that you can simply [run the jar](#running-the-jar-file) to start your ql-node.

## Build from Source Code via Maven (Advanced Installation)

### 1) Requirements

You will need the JDK and Maven. A guide on how to do that on Windows or Ubuntu can be found in [this section](https://github.com/mikrohash/isf-jclient#1-get-the-requirements-jdk--maven).

### 2) Installing the Dependencies

In the following sections we will use git to clone the repositories. But you can also download them [manually](https://github.com/mikrohash/isf-jclient#method-c-manual-download) or [via wget](https://github.com/mikrohash/isf-jclient#method-a-download-via-wget).

#### IOTA Library

```shell
cd /path/to/your/favourite/directory/
git clone https://github.com/iotaledger/iota.lib.java
cd iota.lib.java/
mvn install
```
    
#### Qlite Library

```shell
cd /path/to/your/favourite/directory/
git clone https://github.com/qubiclite/qlite.lib.java
cd qlite.lib.java/  
mvn versions:use-latest-versions -DallowSnapshots=true -DexcludeReactor=false
mvn install
```
    
### 3) Installing the QLRI

```shell
cd /path/to/your/favourite/directory/
git clone https://github.com/qubiclite/qlri
cd qlri/
mvn versions:use-latest-versions -DallowSnapshots=true -DexcludeReactor=false
mvn install
```

If everything went successfully, you should now find a runnable jar file called `qlri-[VERSION].jar` in the `qlri/` directory.

## Build and run with docker

Build a docker image with "qubiclite/qlri:latest" tag
``` shell
docker build -t qubiclite/qlri:latest .  
```

Run the docker image
``` shell
docker run -it -p 17733:17733  qubiclite/qlri
```
Open http://localhost:17733/index.html in your browser.

## Running The Jar File

### Default Start

To run the jar file use this command (you will need the **JRE** or **JDK**):

``` shell
java -jar qlri-[VERSION].jar
```

To make things much more intuitive, it is highly suggest to start the .jar with the `-api` parameter:

``` shell
java -jar qlri-[VERSION].jar -api
```

This will start **Qlite Web** (a web gui, see [screenshots](#screenshots)) that you can access in your browser by visiting `http://[YOUR IP]:17733/index.html`. Additionally you can use `-u username:password` to
protect your API from unauthorized access:

``` shell
java -jar qlri-[VERSION].jar -api -u user:pass
```

### Parameters

You can pass certain paramters with that line

| parameter | alias | example | default value¹ | what it does
| --- | --- | --- | --- | ---
| `-api` | | `-api` | disabled | enables the api and web gui (address will be shown in your terminal)
| `-port` | `-p` | `-port 17733` | 17733 | sets the port of the api (requires api to be enabled)
| `-host` | `-h` | `-host localhost` | your ip | sets a custom host for your api instead of the ip (make sure that the host is available!)
| `-user` | `-u` | `-u tom:hunter2` | no user | adds a user account to the API (public access if no account set)
| `-mainnet` | `-mn` | `-mainnet` | testnet | required when you want to connect through mainnet nodes to the mainnet tangle instead of testnet
| `-node` | `-n` | `-n http://no.de:443` | IF testnet² | changes the iota node that is used to connect to the tangle
| `-remotepow` | `-rp` | `-remotepow` | disabled | outsources the proof-of-work from your qlri to the remote iota node (requires that node to have remote pow activated)

¹ … default value if you do not pass the respective parameter to your jar file.
² … https://nodes.testnet.iota.org:443

## Using the QLRI

### Creating a new Qubic:

This will create a new qubic starting in 180 seconds. Write the ql code into a seperate file.
The path can either be relative to the .jar file (as in the example) or absolute. Type `? qc`
if you want to find out what the other parameters mean.

    $ qc 180 30 30 10 ../my_qubic.ql
    
Now go tell your friends about your awesome qubic, so they can create a new oracle to process it.
Make sure to tell them your qubic's ID. Assuming is is "OCLL…", your friends can now create a new
oracle just for your qubic:

    $ oc OCLLCVOXXHIDWHCOCIDMUHNTWOHKVI9QUBYIWRM9EFSNBCBUFWCV9GPHNGMGDZV9MJFYLYTULMBSIX999

The assembly period is about to end so you want to publish your assembly transaction. To do this, you can first
check which oracles have applied to your qubic:

    $ qla OCL
    
After that, you can select a few and individually add them to the assembly like this:

    $ qaa OCL HMSCIRTQEM9GMRENWOXEJYXARGZNUTLFBTSORUSFU9LYTOLTGGYJXCYAJYGEDTDZVUXRUTPMPSWDWO999
    
After you have added all, you can finally publish the assembly transaction to complete your qubic:

    $ qa OCL
    
**NOTE:** The assembly transaction has to be published before the 180 seconds you set with `qc` are over (otherwise the oracles will abort your qubic)!
    
## Commands

For specific information to a command (parameter details + example use), use `help [command]`.

| command | alias | what it does |
| --- | --- | ---
| `help`                    | `?`   | Helps the user by listing all available commands and providing details to any specific command.
| `node_info`               | `ni`  | Gives details about this ql-node.
| `change_node`             | `cn`  | Changes the IOTA full node used to interact with the tangle.
| `fetch_epoch`             | `fe`  | Determines the quorum based result (consensus) of a qubic's epoch.
| `export`                  | `ex`  | Transforms an entity (iam stream, qubic or oracle) into a string that can be imported again.
| `import`                  | `im`  | Imports a once exported entity (iam stream, qubic or oracle) encoded by a string.
| `qubic_read`              | `qr`  | Reads the specification of any qubic, thus allows the user to analyze that qubic.
| `qubic_list`              | `ql`  | Lists all qubics stored in the persistence.
| `qubic_create`            | `qc`  | Creates a new qubic and stores it in the persistence. life cycle will not be automized: do the assembly transaction manually.
| `qubic_delete`            | `qd`  | Removes a qubic from the persistence (private key will be deleted: cannot be undone).
| `qubic_list_applications` | `qla` | Lists all incoming oracle applications for a specific qubic, response can be used for 'qubic_assembly_add'.
| `qubic_assembly_add`      | `qaa` | Adds an oracle to the assembly as preparation for 'qubic_assemble'.
| `qubic_assemble`          | `qa`  | Publishes the assembly transaction for a specific qubic.
| `qubic_test`              | `qt`  | Runs QL code locally (instead of over the tangle) to allow the author to quickly test whether it works as intended. Limited Functionality (e.g. no qubic_fetch).
| `qubic_quick_run`         | `qqr` | Runs a minimalistic qubic, automates the full qubic life cycle to allow the author to quickly test whether the code works as intended. Only one oracle will be added to the assembly.
| `qubic_consensus`         | `qco` | Determines the quorum based consensus of a qubic's oracle assembly at any IAM index.
| `oracle_create`           | `oc`  | Creates a new oracle and stores it in the persistence. Life cycle will run automically, no more actions required from here on.
| `oracle_delete`           | `od`  | Removes an oracle from the persistence (private key will be deleted, cannot be undone).
| `oracle_list`             | `ol`  | Lists all oracles stored in the persistence
| `oracle_pause`            | `op`  | Temporarily stops an oracle from processing its qubic after the epoch finishes. Can be undone with 'oracle_restart'.
| `oracle_restart`          | `or`  | Restarts an oracle that was paused with 'oracle_pause', makes it process its qubic again.
| `iam_create`              | `ic`  | Creates a new IAM stream and stores it locally in the persistence.
| `iam_delete`              | `id`  | Removes an IAM stream from the persistence (private key will be deleted, cannot be undone).
| `iam_list`                | `il`  | List all IAM streams stored in the persistence.
| `iam_write`               | `iw`  | Writes a message into the iam stream at an index position.
| `iam_read`                | `ir`  | Reads the message of an IAM stream at a certain index.
| `app_list`                | `al`  | Lists all apps installed.
| `app_install`             | `ai`  | Installs an app from an external source.
| `app_uninstall`           | `au`  | Uninstalls an app.



## Screenshots

<img src="http://qubiclite.org/imgs/screenshots/qlite_web_0.png" alt="Qlite Web, gui for your browser" />
<img src="http://qubiclite.org/imgs/screenshots/qlite_web_1.png" alt="Qlite Web supports qApps" />
<img src="http://qubiclite.org/imgs/screenshots/qlri_terminal.png" alt="running the QLRI from terminal" />

## Project Resources

* official project website: http://qubiclite.org
* qApp catalog: http://qame.org
