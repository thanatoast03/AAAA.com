FROM node:18-alpine

# set working directory
WORKDIR /app

# copy package.json and package-lock.json
COPY package*.json ./

# install dependencies
RUN npm install

# copy frontend code
COPY . .

# build w/ parcel
RUN npm run build

# todo: this exposes port; figure out whether it's 3000 or 1234 because of parcel 
EXPOSE 1234

# start app
CMD ["npm", "start"]