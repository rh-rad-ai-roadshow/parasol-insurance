#!/bin/bash -e

if [[ $# -lt 2 ]]; then
  echo "$0: Should have 2 arguments: [source_root target_root]"
  exit 1
fi

source_root=$1
target_root=$2
target_directory="${target_root}/scaffolder-templates/parasol-java-template/skeleton"

sync_prompt_testing() {
  local prompt_testing_source=$source_root/prompt-testing
  local prompt_testing_target=$target_directory/prompt-testing
  echo "Syncing $prompt_testing_source to $prompt_testing_target"

  if [[ ! -d "$prompt_testing_target" ]]; then
    mkdir -p $prompt_testing_target
  fi

  cp -Rfv $prompt_testing_source/ $prompt_testing_target
}

replace_in_file() {
  local filename=$1
  local search_replace_string=$2

  sed -er "$search_replace_string" "$filename" > "${filename}.new" && mv -- "${filename}.new" "$filename"
}

sync_app() {
  local app_source=$source_root/app
  local app_target=$target_directory
  echo "Syncing $app_source to $app_target"

  # 1st copy everything
  cd $app_source && \
    tar --exclude="src/main/webui/node_modules" \
      --exclude="src/main/docker" \
      --exclude="target" \
      --exclude=".quinoa" \
      --exclude="*.iml" \
      --exclude=".dockerignore" \
      --exclude=".gitignore" \
      -cf - . | \
    tar -xf - -C $app_target

  # Now replace whatever needs replacing in $app_source/src/main/resources/application.properties
  app_props_file=$app_target/src/main/resources/application.properties
  replace_in_file "$app_props_file" "s/^[#]*\s*quarkus.http.port=.*/quarkus.http.port=\${{values.port}}/"
  replace_in_file "$app_props_file" "s/^[#]*\s*quarkus.langchain4j.openai.parasol-chat.base-url=.*/quarkus.langchain4j.openai.parasol-chat.base-url=http:\/\/parasol-chat-predictor.aiworkshop.svc.cluster.local:8080\/v1/"
  replace_in_file "$app_props_file" "s/^quarkus.tls.trust-all/# quarkus.tls.trust-all/"
  replace_in_file "$app_props_file" "s/^quarkus.dev-ui.hosts/# quarkus.dev-ui.hosts/"
#  sed -i '' -r "s/^[#]*\s*quarkus.http.port=.*/quarkus.http.port=\${{values.port}}/" $app_props_file
#  sed -i '' -r "s/^[#]*\s*quarkus.langchain4j.openai.parasol-chat.base-url=.*/quarkus.langchain4j.openai.parasol-chat.base-url=http:\/\/parasol-chat-predictor.aiworkshop.svc.cluster.local:8080\/v1/" $app_props_file
#  sed -i '' -r "s/^quarkus.tls.trust-all/# quarkus.tls.trust-all/" $app_props_file
#  sed -i '' -r "s/^quarkus.dev-ui.hosts/# quarkus.dev-ui.hosts/" $app_props_file
}

sync_prompt_testing
sync_app