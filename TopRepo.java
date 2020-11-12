import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TopRepo {

    public static void main(String args[]){
    	
      Scanner scn=new Scanner(System.in);
      System.out.println("Enter The Name of the Organisation");
      String organame=scn.next();
      System.out.println("Enter the value of n");
      int n=scn.nextInt();
      System.out.println("Enter the value of m");
      int m=scn.nextInt();
      String url="https://api.github.com/orgs/"+organame+"/repos";
      StringBuilder orgUrls =getcommit(url);
        List<Repo> nRepo=getnrepo(orgUrls,n);

        StringBuilder mcommits;
        System.out.println("Top "+n+" most popular repositories based on the number of forks");
        System.out.println();
        for (Repo r: nRepo) {
            System.out.println("\tRepository Name : "+r.repoName);
            mcommits=getcommit(r.getContributors());
            countmcont(mcommits,m);
        }
    }

    public static List<Repo> getnrepo(StringBuilder topRepositoryBuilder,int n){
         Pattern repoName=Pattern.compile("\"full_name\":\\s+\"([^\"]+).*");
        Pattern forkCount=Pattern.compile("\"forks_count\":\\s+(\\d+).*");
        Pattern contributors=Pattern.compile("\"contributors_url\":\\s+\"([^\"]+).*");

        Matcher repoNameMatch;
        Matcher forkCountMatch;
        Matcher contributorsMatch;
        
        List<Repo> repoList=new ArrayList<>();
        String repoNametemp=null;
        String contributorstemp=null;
        int forkCounttemp=-1;

        String[] lines = topRepositoryBuilder.toString().split(System.getProperty("line.separator"));

        for(String line: lines){

            repoNameMatch=repoName.matcher(line);
            if(repoNameMatch.find()){

                repoNametemp=repoNameMatch.group(1);
            }

            contributorsMatch=contributors.matcher(line);
            if(contributorsMatch.find()){
            	contributorstemp=contributorsMatch.group(1);
            }

            forkCountMatch=forkCount.matcher(line);
            if(forkCountMatch.find()){
                forkCounttemp= Integer.parseInt(forkCountMatch.group(1));

            }
          
            if(repoNametemp!=null && contributorstemp !=null && forkCounttemp!=-1){
                Repo newRepo=new Repo(repoNametemp,contributorstemp,forkCounttemp);
                repoList.add(newRepo);
                repoNametemp=null;
                contributorstemp=null;
                forkCounttemp=-1;
            }
        }

       
        Collections.sort(repoList);
        List<Repo> topFiveRepo=new ArrayList<>();
        int count=n;
        for (Repo r: repoList) {
             if (count-->0){
                topFiveRepo.add(r);
            }
        }

        return topFiveRepo;
    }


    public static void countmcont(StringBuilder contributorBuilder,int m){
        String[] lines = contributorBuilder.toString().split(System.getProperty("line.separator"));
        Pattern login=Pattern.compile("\"login\":\\s+\"([^\"]+).*");
        Pattern contributions=Pattern.compile("\"contributions\":\\s+(\\d+).*");

        Matcher loginMatch;
        Matcher contributionsMatch;

        List<Committee> committeeList=new ArrayList<>();
        String loginMatchtemp=null;
        int contributionsMatchtemp=-1;
        for(String line: lines){
            loginMatch=login.matcher(line);
            if(loginMatch.find()){
                loginMatchtemp=loginMatch.group(1);
            }


            contributionsMatch=contributions.matcher(line);
            if(contributionsMatch.find()){
             contributionsMatchtemp= Integer.parseInt(contributionsMatch.group(1));
 }

            if(loginMatchtemp!=null && contributionsMatchtemp!=-1){
                Committee newCommittee=new Committee(loginMatchtemp,contributionsMatchtemp);
                committeeList.add(newCommittee);
                loginMatchtemp=null;
                contributionsMatchtemp=-1;
            }
        }

        Collections.sort(committeeList);
        List<Committee> topCommittee=new ArrayList<>();
        int count=m;
        for (Committee c: committeeList) {
            if (count-->0){
                topCommittee.add(c);
            }
        }

        System.out.println("Top "+m+" committees and their commit counts");
        System.out.println("\t\t---------------------------------------");
        for (Committee c:topCommittee) {
            System.out.println("\t\tCommittee Name : "+c.committeeName+"\tCommit Count : "+c.commitCount);
        }
        System.out.println();
    }

    public static StringBuilder getcommit(String url){

            
        String username = "mayankbansal9898";
        String password = "mayank@9898";

       String[] command = {"curl", "-H", "Accept:application/json", "-u", username + ":" + password, url};

        ProcessBuilder process = new ProcessBuilder(command);
        Process p;
        StringBuilder builder = new StringBuilder();
        try {
            p = process.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }

        return builder;
    }

}

class Repo implements Comparable<Repo>{
    String repoName;
    String contributors;
    int forkCount;
    Repo(String repoName, String contributors, int forkCount){
        this.repoName=repoName;
        this.contributors=contributors;
        this.forkCount=forkCount;
    }

    public String getContributors(){
        return contributors;
    }

    public int compareTo(Repo repo) {
        return repo.forkCount-this.forkCount;
    }

}


class Committee implements Comparable<Committee>{

    String committeeName;
    int commitCount;

    Committee(String committeeName, int commitCount){
        this.committeeName=committeeName;
        this.commitCount=commitCount;
    }

    public int compareTo(Committee committee) {
        return committee.commitCount-this.commitCount;
    }
}
