/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

const React = require('react');

const CompLibrary = require('../../core/CompLibrary.js');

const MarkdownBlock = CompLibrary.MarkdownBlock; /* Used to read markdown */
const Container = CompLibrary.Container;
const GridBlock = CompLibrary.GridBlock;

class HomeSplash extends React.Component {
  render() {
    const {siteConfig, language = ''} = this.props;
    const {baseUrl, docsUrl} = siteConfig;
    const docsPart = `${docsUrl ? `${docsUrl}/` : ''}`;
    const langPart = `${language ? `${language}/` : ''}`;
    const docUrl = doc => `${baseUrl}${docsPart}${langPart}${doc}`;

    const SplashContainer = props => (
      <div className="homeContainer">
        <div className="homeSplashFade">
          <div className="wrapper homeWrapper">{props.children}</div>
        </div>
      </div>
    );

    const Logo = props => (
      <div className="projectLogo">
      </div>
    );

    const ProjectTitle = () => (
      <h2 className="projectTitle">
        {siteConfig.title}
        <small>{siteConfig.tagline}</small>
      </h2>
    );

    const PromoSection = props => (
      <div className="section promoSection">
        <div className="promoRow">
          <div className="pluginRowBlock">{props.children}</div>
        </div>
      </div>
    );

    const Button = props => (
      <div className="pluginWrapper buttonWrapper">
        <a className="button" href={props.href} target={props.target}>
          {props.children}
        </a>
      </div>
    );

    return (
      <SplashContainer>
        <div className="inner">
          <ProjectTitle siteConfig={siteConfig} />
          <PromoSection>
            <Button href={docUrl('')}>View Documentation</Button>
          </PromoSection>
        </div>
      </SplashContainer>
    );
  }
}

class Index extends React.Component {
  render() {
    const {config: siteConfig, language = ''} = this.props;
    const {baseUrl} = siteConfig;

    const Block = props => (
      <Container
        padding={['bottom', 'top']}
        id={props.id}
        background={props.background}>
        <GridBlock
          align="center"
          contents={props.children}
          layout={props.layout}
        />
      </Container>
    );

    const FeatureCallout = () => (
      <Container
        className="paddingBottom lightBackground"
        padding={['bottom', 'top']}
        style={{textAlign: 'center'}}>
        <h2>Predefined Modules</h2>
        <MarkdownBlock>
          **Auth**: A fully functional login/signup module, with bearer token and JWT authentication available
        </MarkdownBlock>
        <MarkdownBlock>
          **Permissions**: Assign permissions to users. Other modules can define their own permission categories and give meaning to rules
        </MarkdownBlock>
        <MarkdownBlock>
          **Files**: Track file upload metadata in database and store files using UUIDs
        </MarkdownBlock>
        <MarkdownBlock>
          **Petstore**: For learning purposes, an implementation of the scala-pet-store using h4sm
        </MarkdownBlock>
        <MarkdownBlock>
          **Features**: Users can submit feature requests to your site, and help vote on them
        </MarkdownBlock>
        <MarkdownBlock>
          **Yours**: Build a module you find useful, and submit a PR to have it included in this project!
        </MarkdownBlock>
      </Container>
    );

    const Description = () => (
      <Block background="dark">
        {[
          {
            content:
              'This is another description of how this project is useful',
            image: `${baseUrl}img/undraw_note_list.svg`,
            imageAlign: 'right',
            title: 'Description',
          },
        ]}
      </Block>
    );

    const Features = () => (
      <Block layout="fourColumn">
        {[
          {
            content: 'Modules come with database schema built-in. These are not reference implementations. These are feature complete modules that you can use in your projects now.',
            image: "",
            imageAlign: 'top',
            title: 'Reusable modules',
          },
          {
            content: 'Add functionality in your own modules, by simply linking to data in existing ones via foreign keys',
            image: "",
            imageAlign: 'top',
            title: 'Extendable',
          },
        ]}
      </Block>
    );

    return (
      <div>
        <HomeSplash siteConfig={siteConfig} language={language} />
        <div className="mainContainer">
          <Features />
          <FeatureCallout />
        </div>
      </div>
    );
  }
}

module.exports = Index;
